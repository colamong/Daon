package com.daon.be.community.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageRequestDto {
    
    private Long userId;
    private String message;
    
    public ChatMessageRequestDto(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }
}