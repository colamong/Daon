package com.daon.be.community.dto.response;

import com.daon.be.community.entity.Community;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommunityResponseDto {
    
    private Long id;
    private String title;
    private Integer currentParticipants;
    
    public CommunityResponseDto(Community community) {
        this.id = community.getId();
        this.title = community.getTitle();
        this.currentParticipants = community.getCurrentParticipants();
    }
}