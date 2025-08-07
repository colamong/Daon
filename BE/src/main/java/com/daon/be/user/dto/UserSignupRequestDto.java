package com.daon.be.user.dto;

public record UserSignupRequestDto (
	String email,
	String password,
	String nickname,
	String nationCode
) {}
