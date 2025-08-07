package com.daon.be.user.dto;

public record JwtSigninRequestDto(
	String email,
	String password
) {}

