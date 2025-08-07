package com.daon.be.user.service;

import com.daon.be.user.dto.JwtSigninRequestDto;
import com.daon.be.user.dto.UserSignupRequestDto;
import com.daon.be.user.dto.JwrSigninResponseDto;

public interface UserService {

	void signup(UserSignupRequestDto dto);

	JwrSigninResponseDto signin(JwtSigninRequestDto dto);
}
