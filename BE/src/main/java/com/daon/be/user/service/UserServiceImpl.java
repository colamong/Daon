package com.daon.be.user.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daon.be.user.dto.JwtSigninRequestDto;
import com.daon.be.user.dto.JwrSigninResponseDto;
import com.daon.be.user.dto.NationDto;
import com.daon.be.user.dto.UserProfileUpdateRequestDto;
import com.daon.be.user.dto.UserSignupRequestDto;
import com.daon.be.user.dto.UserWithdrawRequestDto;
import com.daon.be.user.entity.Nation;
import com.daon.be.user.entity.User;
import com.daon.be.user.repository.NationRepository;
import com.daon.be.user.repository.UserRepository;
import com.daon.be.user.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final NationRepository nationRepository;
	private final JwtUtil jwtUtil;

	@Override
	public void signup(UserSignupRequestDto dto) {
		if (userRepository.existsByEmailAndProvider(dto.email(), User.Provider.LOCAL)) {
			throw new IllegalArgumentException("이미 가입된 이메일입니다.");
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

	@Transactional
	public void updateProfile(Long userId, UserProfileUpdateRequestDto dto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		user.setNickname(dto.nickname());
		user.setNationCode(dto.nationCode());
	}

	@Transactional
	public void withdraw(Long userId, UserWithdrawRequestDto dto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		if (user.getProvider() == User.Provider.LOCAL) {
			// 비밀번호 검증
			if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
				throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
			}
		}

		userRepository.delete(user);
	}

	public NationDto getNationByCode(String code) {
		Nation nation = nationRepository.findById(code)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 국가 코드입니다: " + code));
		return NationDto.from(nation);
	}


}
