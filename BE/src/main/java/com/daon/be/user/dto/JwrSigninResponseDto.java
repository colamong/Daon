package com.daon.be.user.dto;

import com.daon.be.user.entity.User;

public record JwrSigninResponseDto(
	Long userId,
	String accessToken,
	String email,
	String nickname
) {
	public static JwrSigninResponseDto of(User user, String token) {
		return new JwrSigninResponseDto(user.getId(), token, user.getEmail(), user.getNickname());
	}
}

