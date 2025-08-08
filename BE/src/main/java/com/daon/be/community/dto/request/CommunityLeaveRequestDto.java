package com.daon.be.community.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommunityLeaveRequestDto {
    
    private Long userId;
    
    public CommunityLeaveRequestDto(Long userId) {
        this.userId = userId;
    }
}