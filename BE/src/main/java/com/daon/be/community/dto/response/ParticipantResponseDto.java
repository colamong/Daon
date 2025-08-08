package com.daon.be.community.dto.response;

import com.daon.be.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParticipantResponseDto {
    
    private Long userId;
    private String userName;
    private String email;
    
    public ParticipantResponseDto(User user) {
        this.userId = user.getId();
        this.userName = user.getNickname();
        this.email = user.getEmail();
    }
}