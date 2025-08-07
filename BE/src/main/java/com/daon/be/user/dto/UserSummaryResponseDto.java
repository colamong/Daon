package com.daon.be.user.dto;

import com.daon.be.user.entity.User;

public record UserSummaryResponseDto(
	String nickname
) {
	public static UserSummaryResponseDto from(User user) {
		return new UserSummaryResponseDto(user.getNickname());
	}
}

