package com.daon.be.community.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommunityListResponseDto {
    
    private List<CommunityResponseDto> communities;
    
    public CommunityListResponseDto(List<CommunityResponseDto> communities) {
        this.communities = communities;
    }
}