package com.daon.be.community.service;

import com.daon.be.community.dto.request.ChatMessageRequestDto;
import com.daon.be.community.dto.response.ChatMessageResponseDto;

import java.util.List;

public interface ChatMessageService {
    
    ChatMessageResponseDto sendMessage(Long communityId, ChatMessageRequestDto requestDto);
    
    List<ChatMessageResponseDto> getMessagesByCommunityId(Long communityId);
    
    List<ChatMessageResponseDto> getMessagesByUserId(Long userId);
}