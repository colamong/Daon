package com.daon.be.community.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommunitySearchRequestDto {
    
    private String title;
    
    public CommunitySearchRequestDto(String title) {
        this.title = title;
    }
}