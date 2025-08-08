package com.daon.be.community.service;

import com.daon.be.community.dto.response.CommunityListResponseDto;
import com.daon.be.community.dto.response.CommunityResponseDto;
import com.daon.be.community.dto.response.ParticipantResponseDto;

import java.util.List;

public interface CommunityService {
    
    CommunityListResponseDto getAllCommunities();
    
    CommunityListResponseDto searchCommunitiesByTitle(String title);
    
    CommunityListResponseDto getActiveCommunitiesByUserId(Long userId);
    
    void joinCommunity(Long communityId, Long userId);
    
    void leaveCommunity(Long communityId, Long userId);
    
    List<ParticipantResponseDto> getParticipants(Long communityId);
    
    CommunityResponseDto getCommunityById(Long communityId);
}