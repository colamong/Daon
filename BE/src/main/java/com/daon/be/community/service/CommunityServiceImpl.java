package com.daon.be.community.service;

import com.daon.be.community.dto.response.CommunityListResponseDto;
import com.daon.be.community.dto.response.CommunityResponseDto;
import com.daon.be.community.dto.response.ParticipantResponseDto;
import com.daon.be.community.entity.ChatMessage;
import com.daon.be.community.entity.Community;
import com.daon.be.community.entity.CommunityParticipation;
import com.daon.be.community.repository.ChatMessageRepository;
import com.daon.be.community.repository.CommunityParticipationRepository;
import com.daon.be.community.repository.CommunityRepository;
import com.daon.be.user.entity.User;
import com.daon.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {
    
    private final CommunityRepository communityRepository;
    private final CommunityParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Override
    @Transactional(readOnly = true)
    public CommunityListResponseDto getAllCommunities() {
        List<Community> communities = communityRepository.findAllOrderByCurrentParticipantsDesc();
        List<CommunityResponseDto> communityDtos = communities.stream()
                .map(CommunityResponseDto::new)
                .collect(Collectors.toList());
        return new CommunityListResponseDto(communityDtos);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CommunityListResponseDto searchCommunitiesByTitle(String title) {
        List<Community> communities = communityRepository.findByTitleContaining(title);
        List<CommunityResponseDto> communityDtos = communities.stream()
                .map(CommunityResponseDto::new)
                .collect(Collectors.toList());
        return new CommunityListResponseDto(communityDtos);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CommunityListResponseDto getActiveCommunitiesByUserId(Long userId) {
        List<CommunityParticipation> participations = participationRepository.findByUserIdAndLeftAtIsNull(userId);
        List<CommunityResponseDto> communityDtos = participations.stream()
                .map(participation -> new CommunityResponseDto(participation.getCommunity()))
                .collect(Collectors.toList());
        return new CommunityListResponseDto(communityDtos);
    }
    
    @Override
    @Transactional
    public void joinCommunity(Long communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<CommunityParticipation> existingParticipation = 
                participationRepository.findByCommunityAndUserAndLeftAtIsNull(community, user);
        
        if (existingParticipation.isPresent()) {
            throw new RuntimeException("User is already participating in this community");
        }
        
        // 현재 참여 중인지 확인 후, 참여하지 않은 상태에서 입장하는 경우에만 입장 메시지 표시
        // (과거 참여 이력과 관계없이 현재 상태에서 새로 입장하는 경우)
        boolean shouldShowJoinMessage = true; // 현재 참여 중이 아니었으므로 입장 메시지 표시
        
        // 한국 시간대 사용
        ZonedDateTime koreaTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime koreaLocalTime = koreaTime.toLocalDateTime();
        
        CommunityParticipation participation = new CommunityParticipation(community, user, koreaLocalTime);
        participationRepository.save(participation);
        
        community.setCurrentParticipants(community.getCurrentParticipants() + 1);
        communityRepository.save(community);
        
        if (shouldShowJoinMessage) {
            createAndSendJoinMessage(community, user);
        }
    }
    
    @Override
    @Transactional
    public void leaveCommunity(Long communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        CommunityParticipation participation = participationRepository.findByCommunityAndUserAndLeftAtIsNull(community, user)
                .orElseThrow(() -> new RuntimeException("User is not participating in this community"));
        
        // 한국 시간대 사용
        ZonedDateTime koreaTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime koreaLocalTime = koreaTime.toLocalDateTime();
        
        participation.setLeftAt(koreaLocalTime);
        participationRepository.save(participation);
        
        community.setCurrentParticipants(community.getCurrentParticipants() - 1);
        communityRepository.save(community);
        
        createAndSendLeaveMessage(community, user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ParticipantResponseDto> getParticipants(Long communityId) {
        List<CommunityParticipation> participations = 
                participationRepository.findByCommunityIdAndLeftAtIsNull(communityId);
        return participations.stream()
                .map(participation -> new ParticipantResponseDto(participation.getUser()))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CommunityResponseDto getCommunityById(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        return new CommunityResponseDto(community);
    }
    
    private void createAndSendJoinMessage(Community community, User user) {
        String joinMessage = user.getNickname() + "님이 채팅방에 입장하셨습니다.";
        
        // 한국 시간대 사용으로 수정
        ZonedDateTime koreaTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime koreaLocalTime = koreaTime.toLocalDateTime();
        
        ChatMessage systemMessage = new ChatMessage(
                community, 
                user, 
                joinMessage, 
                koreaLocalTime,  // LocalDateTime.now() 대신 koreaLocalTime 사용
                ChatMessage.MessageType.SYSTEM_JOIN
        );
        
        ChatMessage savedMessage = chatMessageRepository.save(systemMessage);
        
        messagingTemplate.convertAndSend(
                "/topic/community/" + community.getId(), 
                convertToResponseDto(savedMessage)
        );
    }
    
    private void createAndSendLeaveMessage(Community community, User user) {
        String leaveMessage = user.getNickname() + "님이 채팅방을 나가셨습니다.";
        
        // 한국 시간대 사용으로 수정
        ZonedDateTime koreaTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime koreaLocalTime = koreaTime.toLocalDateTime();
        
        ChatMessage systemMessage = new ChatMessage(
                community, 
                user, 
                leaveMessage, 
                koreaLocalTime,  // LocalDateTime.now() 대신 koreaLocalTime 사용
                ChatMessage.MessageType.SYSTEM_LEAVE
        );
        
        ChatMessage savedMessage = chatMessageRepository.save(systemMessage);
        
        messagingTemplate.convertAndSend(
                "/topic/community/" + community.getId(), 
                convertToResponseDto(savedMessage)
        );
    }
    
    private com.daon.be.community.dto.response.ChatMessageResponseDto convertToResponseDto(ChatMessage chatMessage) {
        com.daon.be.community.dto.response.ChatMessageResponseDto dto = 
                new com.daon.be.community.dto.response.ChatMessageResponseDto(chatMessage);
        
        String profileImg = chatMessage.getUser().getProfileImg();
        if (profileImg != null && !profileImg.isEmpty()) {
            dto.setUserProfileImg(profileImg);
        }
        
        return dto;
    }
}