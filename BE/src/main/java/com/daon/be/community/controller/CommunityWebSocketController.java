package com.daon.be.community.controller;

import com.daon.be.community.dto.request.ChatMessageRequestDto;
import com.daon.be.community.dto.response.ChatMessageResponseDto;
import com.daon.be.community.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CommunityWebSocketController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    
    @MessageMapping("/community/{communityId}")
    public void sendMessage(@DestinationVariable Long communityId, ChatMessageRequestDto messageDto) {
        try {
            ChatMessageResponseDto savedMessage = chatMessageService.sendMessage(communityId, messageDto);
            
            messagingTemplate.convertAndSend("/topic/community/" + communityId, savedMessage);
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
}