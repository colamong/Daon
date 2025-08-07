package com.daon.be.user.dto;

public record UserProfileUpdateRequestDto(
	String nickname,
	String nationCode
) {}
