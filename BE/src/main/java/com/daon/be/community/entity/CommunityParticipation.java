package com.daon.be.community.entity;

import com.daon.be.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_participation")
@Getter
@Setter
@NoArgsConstructor
public class CommunityParticipation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "entered_at")
    private LocalDateTime enteredAt;
    
    @Column(name = "left_at")
    private LocalDateTime leftAt;
    
    public CommunityParticipation(Community community, User user, LocalDateTime enteredAt) {
        this.community = community;
        this.user = user;
        this.enteredAt = enteredAt;
    }
}