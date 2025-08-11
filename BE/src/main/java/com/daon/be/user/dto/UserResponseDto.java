// com.daon.be.user.dto.UserResponseDto
package com.daon.be.user.dto;

import com.daon.be.user.entity.User;

public record UserResponseDto(
		Long id,
		String email,
		String nickname,
		String nationCode,
		String provider
) {
	public static UserResponseDto fromEntity(User user) {
		return new UserResponseDto(
				user.getId(),
				user.getEmail(),
				user.getNickname(),
				user.getNationCode(),
				user.getProvider().name()
		);
	}
}
