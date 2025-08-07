package com.daon.be.user.controller;

import java.time.Duration;
import java.util.List;

import com.daon.be.user.auth.SigninUser;
import com.daon.be.user.dto.JwtSigninRequestDto;
import com.daon.be.user.dto.NationDto;
import com.daon.be.user.dto.UserProfileUpdateRequestDto;
import com.daon.be.user.dto.UserResponseDto;
import com.daon.be.user.dto.UserSignupRequestDto;
import com.daon.be.user.dto.JwrSigninResponseDto;
import com.daon.be.user.dto.UserSummaryResponseDto;
import com.daon.be.user.dto.UserWithdrawRequestDto;
import com.daon.be.user.entity.Nation;
import com.daon.be.user.entity.User;
import com.daon.be.user.repository.NationRepository;
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
	private final NationRepository nationRepository;

	@PostMapping("/signup")
	public ResponseEntity<Void> signup(@RequestBody UserSignupRequestDto dto) {
		userService.signup(dto);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/nations")
	public ResponseEntity<List<NationDto>> getAllNations() {
		List<NationDto> result = nationRepository.findAll()
			.stream()
			.map(NationDto::from)
			.toList();

		return ResponseEntity.ok(result);
	}

	@GetMapping("/nation/{code}")
	public ResponseEntity<NationDto> getNation(@PathVariable String code) {
		NationDto nation = userService.getNationByCode(code);
		return ResponseEntity.ok(nation);
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

	@PutMapping("/profile")
	public ResponseEntity<Void> updateProfile(
		@SigninUser User user,
		@RequestBody UserProfileUpdateRequestDto dto
	) {
		userService.updateProfile(user.getId(), dto);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/summary")
	public ResponseEntity<UserSummaryResponseDto> getUserSummary(@SigninUser User user) {
		return ResponseEntity.ok(UserSummaryResponseDto.from(user));
	}

	@DeleteMapping("/withdraw")
	public ResponseEntity<Void> withdraw(@SigninUser User user, @RequestBody UserWithdrawRequestDto dto) {
		userService.withdraw(user.getId(), dto);

		// accessToken 쿠키 삭제
		ResponseCookie deleteCookie = ResponseCookie.from("accessToken", "")
			.path("/")
			.httpOnly(true)
			.maxAge(0)
			.build();

		return ResponseEntity.noContent()
			.header("Set-Cookie", deleteCookie.toString())
			.build();
	}


}
