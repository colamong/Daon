package com.daon.be.community.dto.response;

import com.daon.be.community.entity.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageResponseDto {
    
    private Long id;
    private Long communityId;
    private Long userId;
    private String userName;
    private String message;
    private LocalDateTime sentAt;
    
    public ChatMessageResponseDto(ChatMessage chatMessage) {
        this.id = chatMessage.getId();
        this.communityId = chatMessage.getCommunity().getId();
        this.userId = chatMessage.getUser().getId();
        this.userName = chatMessage.getUser().getNickname();
        this.message = chatMessage.getMessage();
        this.sentAt = chatMessage.getSentAt();
    }
}