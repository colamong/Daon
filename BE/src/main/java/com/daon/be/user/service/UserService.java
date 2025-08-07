package com.daon.be.user.service;

import com.daon.be.user.dto.JwtSigninRequestDto;
import com.daon.be.user.dto.NationDto;
import com.daon.be.user.dto.UserProfileUpdateRequestDto;
import com.daon.be.user.dto.UserSignupRequestDto;
import com.daon.be.user.dto.JwrSigninResponseDto;
import com.daon.be.user.dto.UserWithdrawRequestDto;

public interface UserService {

	void signup(UserSignupRequestDto dto);

	JwrSigninResponseDto signin(JwtSigninRequestDto dto);

	public void updateProfile(Long userId, UserProfileUpdateRequestDto dto);

	public void withdraw(Long userId, UserWithdrawRequestDto dto);

	public NationDto getNationByCode(String code);

}
