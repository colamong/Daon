package com.daon.be.user.dto;

public record UserWithdrawRequestDto(
	String password // LOCAL 사용자만 사용, GOOGLE은 무시
) {}

