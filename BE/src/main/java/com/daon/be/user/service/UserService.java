package com.daon.be.user.service;

import com.daon.be.user.dto.JwtSigninRequestDto;
import com.daon.be.user.dto.NationDto;
import com.daon.be.user.dto.UserProfileUpdateRequestDto;
import com.daon.be.user.dto.UserSignupRequestDto;
import com.daon.be.user.dto.JwrSigninResponseDto;
import com.daon.be.user.dto.UserWithdrawRequestDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

	void signup(UserSignupRequestDto dto);

	JwrSigninResponseDto signin(JwtSigninRequestDto dto);

	void updateProfile(Long userId, UserProfileUpdateRequestDto dto);

	void withdraw(Long userId, UserWithdrawRequestDto dto);

	NationDto getNationByCode(String code);

	// 프로필 이미지 업로드/교체 (멀티파트)
	void updateProfileImage(Long userId, MultipartFile file);
}
