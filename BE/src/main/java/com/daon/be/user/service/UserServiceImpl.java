package com.daon.be.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.daon.be.user.dto.JwtSigninRequestDto;
import com.daon.be.user.dto.JwrSigninResponseDto;
import com.daon.be.user.dto.UserSignupRequestDto;
import com.daon.be.user.entity.User;
import com.daon.be.user.repository.UserRepository;
import com.daon.be.user.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	public void signup(UserSignupRequestDto dto) {
		if (userRepository.existsByEmail(dto.email())) {
			throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
		}

		String encodedPass = passwordEncoder.encode(dto.password());

		User user = User.of(
			dto.email(),
			encodedPass,
			dto.nickname(),
			dto.nationCode()
		);

		userRepository.save(user);
	}

	@Override
	public JwrSigninResponseDto signin (JwtSigninRequestDto dto) {
		User user = userRepository.findByEmail(dto.email())
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

		if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}

		String token = jwtUtil.generateToken(user.getId());
		return JwrSigninResponseDto.of(user, token);
	}


}
