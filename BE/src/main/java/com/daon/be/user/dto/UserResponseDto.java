package com.daon.be.user.dto;

import com.daon.be.user.entity.User;
import com.daon.be.global.infra.S3Uploader;

public record UserResponseDto(
		Long id,
		String email,
		String nickname,
		String nationCode,
		String provider,
		String profileImg
) {
	public static UserResponseDto fromEntity(User user) {
		return new UserResponseDto(
				user.getId(),
				user.getEmail(),
				user.getNickname(),
				user.getNationCode(),
				user.getProvider().name(),
				user.getProfileImg()
		);
	}

	// S3Uploader를 사용하여 프리사인 URL 생성
	public static UserResponseDto fromEntity(User user, S3Uploader s3Uploader) {
		String profileImgUrl = null;
		if (user.getProfileImg() != null && !user.getProfileImg().isBlank()) {
			profileImgUrl = s3Uploader.presignGetUrl(user.getProfileImg());
		}

		return new UserResponseDto(
				user.getId(),
				user.getEmail(),
				user.getNickname(),
				user.getNationCode(),
				user.getProvider().name(),
				profileImgUrl
		);
	}
}
