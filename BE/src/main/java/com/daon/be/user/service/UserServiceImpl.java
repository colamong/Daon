package com.daon.be.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

import com.daon.be.global.infra.S3Uploader;
import com.daon.be.user.dto.JwtSigninRequestDto;
import com.daon.be.user.dto.JwrSigninResponseDto;
import com.daon.be.user.dto.NationDto;
import com.daon.be.user.dto.UserProfileUpdateRequestDto;
import com.daon.be.user.dto.UserSignupRequestDto;
import com.daon.be.user.dto.UserWithdrawRequestDto;
import com.daon.be.user.entity.Nation;
import com.daon.be.user.entity.User;
import com.daon.be.user.jwt.JwtUtil;
import com.daon.be.user.repository.NationRepository;
import com.daon.be.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final NationRepository nationRepository;
	private final JwtUtil jwtUtil;

	// S3
	private final S3Uploader s3Uploader;
	private final S3Client s3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

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

	// 텍스트 프로필 수정
	@Override
	@Transactional
	public void updateProfile(Long userId, UserProfileUpdateRequestDto dto) {
		if (userId == null) throw new IllegalArgumentException("userId가 없습니다");

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		// 동일 사용자 검증 (구조상 동일인이므로 형태만 유지)
		if (!user.getId().equals(userId)) {
			throw new AccessDeniedException("권한이 없습니다.");
		}

		// 닉네임 갱신 (null/blank 방지)
		if (dto.nickname() != null && !dto.nickname().isBlank()) {
			user.setNickname(dto.nickname());
		}

		// nationCode 갱신 — 존재 여부 확인 후 반영
		if (dto.nationCode() != null && !dto.nationCode().isBlank()) {
			boolean exists = nationRepository.existsById(dto.nationCode());
			if (!exists) {
				throw new IllegalArgumentException("국가 코드를 찾을 수 없습니다: " + dto.nationCode());
			}
			// (User 엔티티가 문자열 코드 필드를 가진 경우)
			user.setNationCode(dto.nationCode());

			// 만약 User가 Nation 엔티티 연관관계를 가진 구조라면 아래로 변경하세요:
			// Nation nation = nationRepository.findById(dto.nationCode())
			//     .orElseThrow(() -> new IllegalArgumentException("국가 코드를 찾을 수 없습니다: " + dto.nationCode()));
			// user.setNation(nation);
		}
		// JPA 변경감지로 자동 업데이트 (save 호출 불필요)
	}

	@Override
	@Transactional
	public void withdraw(Long userId, UserWithdrawRequestDto dto) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		if (user.getProvider() == User.Provider.LOCAL) {
			if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
				throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
			}
		}
		userRepository.delete(user);
	}

	@Override
	public NationDto getNationByCode(String code) {
		Nation nation = nationRepository.findById(code)
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 국가 코드입니다: " + code));
		return NationDto.from(nation);
	}

	// 프로필 이미지 업로드/교체 (멀티파트)
	@Override
	@Transactional
	public void updateProfileImage(Long userId, MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("파일이 비어 있습니다.");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		if (!user.getId().equals(userId)) {
			throw new AccessDeniedException("권한이 없습니다.");
		}

		// 1) 기존 이미지 삭제
		String old = user.getProfileImg();
		if (old != null && !old.isBlank()) {
			try {
				String keyToDelete = extractKey(old);
				if (keyToDelete != null) {
					s3Client.deleteObject(DeleteObjectRequest.builder()
							.bucket(bucket)
							.key(keyToDelete)
							.build());
				}
			} catch (Exception ignore) {} // 삭제 실패해도 신규 업로드 진행
		}

		// 2) 신규 업로드
		String ext = extOf(file.getOriginalFilename());
		String key = "user/%d/%s.%s".formatted(userId, UUID.randomUUID(), ext);
		try {
			String savedKey = s3Uploader.upload(key, file.getBytes(), file.getContentType());
			user.setProfileImg(savedKey); // DB에는 key만 저장
		} catch (IOException e) {
			throw new RuntimeException("프로필 이미지 업로드 실패", e);
		}
	}

	// 파일 확장자 추출 (없으면 jpg)
	private static String extOf(String name) {
		int i = (name == null) ? -1 : name.lastIndexOf('.');
		return (i > -1) ? name.substring(i + 1).toLowerCase() : "jpg";
	}

	// 'https://bucket.s3....amazonaws.com/KEY' → 'KEY'
	private String extractKey(String stored) {
		if (stored == null || stored.isBlank()) return null;
		if (stored.startsWith("http")) {
			int idx = stored.indexOf(".amazonaws.com/");
			if (idx > -1) return stored.substring(idx + ".amazonaws.com/".length());
			int last = stored.lastIndexOf('/');
			return last > -1 ? stored.substring(last + 1) : stored;
		}
		return stored; // 이미 key라고 가정
	}
}
