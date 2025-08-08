package com.daon.be.community.service;

import com.daon.be.community.dto.request.ChatMessageRequestDto;
import com.daon.be.community.dto.response.ChatMessageResponseDto;
import com.daon.be.community.entity.ChatMessage;
import com.daon.be.community.entity.Community;
import com.daon.be.community.repository.ChatMessageRepository;
import com.daon.be.community.repository.CommunityRepository;
import com.daon.be.user.entity.User;
import com.daon.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    
    private final ChatMessageRepository chatMessageRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public ChatMessageResponseDto sendMessage(Long communityId, ChatMessageRequestDto requestDto) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        ChatMessage chatMessage = new ChatMessage(community, user, requestDto.getMessage(), LocalDateTime.now());
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        
        return new ChatMessageResponseDto(savedMessage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getMessagesByCommunityId(Long communityId) {
        List<ChatMessage> messages = chatMessageRepository.findByCommunityIdOrderBySentAtAsc(communityId);
        return messages.stream()
                .map(ChatMessageResponseDto::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponseDto> getMessagesByUserId(Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findByUserIdOrderBySentAtDesc(userId);
        return messages.stream()
                .map(ChatMessageResponseDto::new)
                .collect(Collectors.toList());
    }
}