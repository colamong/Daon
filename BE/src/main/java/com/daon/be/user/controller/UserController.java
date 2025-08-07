package com.daon.be.user.controller;

import java.time.Duration;

import com.daon.be.user.auth.SigninUser;
import com.daon.be.user.dto.JwtSigninRequestDto;
import com.daon.be.user.dto.UserResponseDto;
import com.daon.be.user.dto.UserSignupRequestDto;
import com.daon.be.user.dto.JwrSigninResponseDto;
import com.daon.be.user.entity.User;
import com.daon.be.user.repository.UserRepository;
import com.daon.be.user.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

	private final UserService userService;
	private final UserRepository userRepository;

	@PostMapping("/signup")
	public ResponseEntity<Void> signup(@RequestBody UserSignupRequestDto request) {
		userService.signup(request);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/signin")
	public ResponseEntity<JwrSigninResponseDto> signin(@RequestBody JwtSigninRequestDto dto, HttpServletResponse response ) {
		JwrSigninResponseDto jwtResponse = userService.signin(dto);

		ResponseCookie cookie = ResponseCookie.from("accessToken", jwtResponse.accessToken())
			.httpOnly(true)
			.secure(false)  // 배포 환경이면 true (HTTPS)
			.path("/")
			.maxAge(Duration.ofHours(8))
			.sameSite("Lax")
			.build();

		response.addHeader("Set-Cookie", cookie.toString());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/me")
	public ResponseEntity<UserResponseDto> me(@SigninUser Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

		return ResponseEntity.ok(UserResponseDto.fromEntity(user));
	}

	@PostMapping("/signout")
	public ResponseEntity<Void> signout(HttpServletResponse response) {
		ResponseCookie expiredCookie = ResponseCookie.from("accessToken", "")
			.httpOnly(true)
			.path("/")
			.maxAge(0)          // 쿠키 만료
			.build();

		response.addHeader("Set-Cookie", expiredCookie.toString());
		return ResponseEntity.ok().build();
	}


}
