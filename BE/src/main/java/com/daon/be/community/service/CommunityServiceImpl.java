package com.daon.be.community.service;

import com.daon.be.community.dto.response.CommunityListResponseDto;
import com.daon.be.community.dto.response.CommunityResponseDto;
import com.daon.be.community.dto.response.ParticipantResponseDto;
import com.daon.be.community.entity.Community;
import com.daon.be.community.entity.CommunityParticipation;
import com.daon.be.community.repository.CommunityParticipationRepository;
import com.daon.be.community.repository.CommunityRepository;
import com.daon.be.user.entity.User;
import com.daon.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {
    
    private final CommunityRepository communityRepository;
    private final CommunityParticipationRepository participationRepository;
    private final UserRepository userRepository;
    
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
        
        CommunityParticipation participation = new CommunityParticipation(community, user, LocalDateTime.now());
        participationRepository.save(participation);
        
        community.setCurrentParticipants(community.getCurrentParticipants() + 1);
        communityRepository.save(community);
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
        
        participation.setLeftAt(LocalDateTime.now());
        participationRepository.save(participation);
        
        community.setCurrentParticipants(community.getCurrentParticipants() - 1);
        communityRepository.save(community);
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
}