package com.daon.be.user.controller;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.daon.be.user.auth.SigninUser;
import com.daon.be.user.dto.*;
import com.daon.be.user.entity.User;
import com.daon.be.user.repository.NationRepository;
import com.daon.be.user.repository.UserRepository;
import com.daon.be.user.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j
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
	public ResponseEntity<JwrSigninResponseDto> signin(@RequestBody JwtSigninRequestDto dto,
													   HttpServletResponse response) {
		JwrSigninResponseDto jwtResponse = userService.signin(dto);

		ResponseCookie cookie = ResponseCookie.from("accessToken", jwtResponse.accessToken())
				.httpOnly(true)
				.secure(false) // 배포면 true
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
				.maxAge(0)
				.build();
		response.addHeader("Set-Cookie", expiredCookie.toString());
		return ResponseEntity.ok().build();
	}

	// @SigninUser Long userId 로 확정적으로 id 수신 후 서비스 호출
	@PutMapping("/profile")
	public ResponseEntity<?> updateProfile(@SigninUser Long userId,
										   @RequestBody UserProfileUpdateRequestDto dto) {
		log.info("PUT /api/user/profile userId={} payload={}", userId, dto);
		if (userId == null) {
			return ResponseEntity.badRequest().body(Map.of("message", "로그인 정보가 없습니다"));
		}
		try {
			userService.updateProfile(userId, dto);
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		} catch (Exception e) {
			log.error("updateProfile error", e);
			return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
		}
	}

	// 프로필 이미지 업로드/교체 (멀티파트)
	@PutMapping(value = "/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateProfileImage(@SigninUser Long userId,
												@RequestPart("file") MultipartFile file) {
		log.info("PUT /api/user/profile/image userId={} fileName={}", userId,
				(file != null ? file.getOriginalFilename() : "null"));

		if (userId == null) {
			return ResponseEntity.status(401).body(Map.of("message", "로그인 정보가 없습니다"));
		}
		if (file == null || file.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("message", "파일이 비어 있습니다"));
		}
		try {
			userService.updateProfileImage(userId, file);
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		} catch (Exception e) {
			log.error("updateProfileImage error", e);
			return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
		}
	}

	@GetMapping("/summary")
	public ResponseEntity<UserSummaryResponseDto> getUserSummary(@SigninUser User user) {
		return ResponseEntity.ok(UserSummaryResponseDto.from(user));
	}

	@DeleteMapping("/withdraw")
	public ResponseEntity<Void> withdraw(@SigninUser User user,
										 @RequestBody UserWithdrawRequestDto dto) {
		userService.withdraw(user.getId(), dto);

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
