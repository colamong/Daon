package com.daon.be.community.service;

import com.daon.be.community.dto.request.ChatMessageRequestDto;
import com.daon.be.community.dto.response.ChatMessageResponseDto;
import com.daon.be.community.entity.ChatMessage;
import com.daon.be.community.entity.Community;
import com.daon.be.community.entity.CommunityParticipation;
import com.daon.be.community.repository.ChatMessageRepository;
import com.daon.be.community.repository.CommunityRepository;
import com.daon.be.community.repository.CommunityParticipationRepository;
import com.daon.be.global.infra.S3Uploader;
import com.daon.be.user.entity.User;
import com.daon.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
public class ChatMessageServiceImpl implements ChatMessageService {
    
    private final ChatMessageRepository chatMessageRepository;
    private final CommunityRepository communityRepository;
    private final CommunityParticipationRepository communityParticipationRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;
    
    @Override
    @Transactional
    public ChatMessageResponseDto sendMessage(Long communityId, ChatMessageRequestDto requestDto) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 한국 시간대 사용
        ZonedDateTime koreaTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime koreaLocalTime = koreaTime.toLocalDateTime();
        
        ChatMessage chatMessage = new ChatMessage(community, user, requestDto.getMessage(), koreaLocalTime);
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        
        return convertToResponseDto(savedMessage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getMessagesByCommunityId(Long communityId) {
        List<ChatMessage> messages = chatMessageRepository.findByCommunityIdOrderBySentAtAsc(communityId);
        return messages.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getMessagesByCommunityIdForUser(Long communityId, Long userId) {
        // 사용자의 채팅방 참여 정보 조회
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<CommunityParticipation> participation = 
                communityParticipationRepository.findByCommunityAndUserAndLeftAtIsNull(community, user);
        
        if (participation.isEmpty()) {
            // 참여하지 않은 사용자는 빈 리스트 반환
            return List.of();
        }
        
        LocalDateTime enteredAt = participation.get().getEnteredAt();
        
        // 참여 시점 이후의 메시지만 조회
        List<ChatMessage> messages = chatMessageRepository.findByCommunityIdAndSentAtGreaterThanEqualOrderBySentAtAsc(
                communityId, enteredAt);
        
        return messages.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getMessagesByUserId(Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findByUserIdOrderBySentAtDesc(userId);
        return messages.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    // ChatMessage를 ChatMessageResponseDto로 변환 (S3 URL 포함)
    private ChatMessageResponseDto convertToResponseDto(ChatMessage chatMessage) {
        ChatMessageResponseDto dto = new ChatMessageResponseDto(chatMessage);
        
        // S3 key를 presigned URL로 변환
        String profileImg = chatMessage.getUser().getProfileImg();
        if (profileImg != null && !profileImg.isEmpty()) {
            String presignedUrl = s3Uploader.presignGetUrl(profileImg);
            dto.setUserProfileImg(presignedUrl);
        }
        
        return dto;
    }
}