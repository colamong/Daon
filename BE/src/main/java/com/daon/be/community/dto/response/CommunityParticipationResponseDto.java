package com.daon.be.community.dto.response;

import com.daon.be.community.entity.CommunityParticipation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommunityParticipationResponseDto {
    
    private Long id;
    private Long communityId;
    private String communityTitle;
    private Long userId;
    private LocalDateTime enteredAt;
    private LocalDateTime leftAt;
    
    public CommunityParticipationResponseDto(CommunityParticipation participation) {
        this.id = participation.getId();
        this.communityId = participation.getCommunity().getId();
        this.communityTitle = participation.getCommunity().getTitle();
        this.userId = participation.getUser().getId();
        this.enteredAt = participation.getEnteredAt();
        this.leftAt = participation.getLeftAt();
    }
}