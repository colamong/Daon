package com.daon.be.community.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommunityJoinRequestDto {
    
    private Long userId;
    
    public CommunityJoinRequestDto(Long userId) {
        this.userId = userId;
    }
}