package com.daon.be.community.dto.response;

import com.daon.be.community.entity.ChatMessage;
import com.daon.be.community.entity.ChatMessage.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageResponseDto {

    private Long id;
    private Long communityId;
    private Long userId;
    private String userName;
    private String userProfileImg;
    private String message;
    private OffsetDateTime sentAt; // LocalDateTime → OffsetDateTime(UTC) 변경
    private MessageType messageType;

    public ChatMessageResponseDto(ChatMessage chatMessage) {
        this.id = chatMessage.getId();
        this.communityId = chatMessage.getCommunity().getId();
        this.userId = chatMessage.getUser().getId();
        this.userName = chatMessage.getUser().getNickname();
        this.userProfileImg = chatMessage.getUser().getProfileImg();
        this.message = chatMessage.getMessage();
        // LocalDateTime → OffsetDateTime(UTC) 변환
        this.sentAt = chatMessage.getSentAt() != null
                ? chatMessage.getSentAt().atOffset(ZoneOffset.UTC)
                : null;
        this.messageType = chatMessage.getMessageType();
    }
}
