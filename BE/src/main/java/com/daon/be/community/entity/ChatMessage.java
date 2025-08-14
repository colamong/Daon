package com.daon.be.community.entity;

import com.daon.be.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType = MessageType.USER;
    
    public ChatMessage(Community community, User user, String message, LocalDateTime sentAt) {
        this.community = community;
        this.user = user;
        this.message = message;
        this.sentAt = sentAt;
        this.messageType = MessageType.USER;
    }
    
    public ChatMessage(Community community, User user, String message, LocalDateTime sentAt, MessageType messageType) {
        this.community = community;
        this.user = user;
        this.message = message;
        this.sentAt = sentAt;
        this.messageType = messageType;
    }
    
    public enum MessageType {
        USER, SYSTEM_JOIN, SYSTEM_LEAVE
    }
}