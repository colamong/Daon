package com.daon.be;

import com.daon.be.user.dto.JwtSigninRequestDto;
import com.daon.be.user.dto.UserSignupRequestDto;
import com.daon.be.user.entity.User;
import com.daon.be.user.jwt.JwtUtil;
import com.daon.be.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestS3Config.class)
@Transactional
class UserAuthIntegrationTest {

	@Autowired MockMvc mockMvc;
	ObjectMapper objectMapper = new ObjectMapper(); // 직접 생성
	@Autowired UserRepository userRepository;
	@Autowired JwtUtil jwtUtil;

	@Test
	@DisplayName("회원가입 성공")
	void signup() throws Exception {
		var dto = new UserSignupRequestDto("user@test.com", "1234pass", "닉네임", "KR");

		mockMvc.perform(post("/api/user/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("로그인 시 accessToken 쿠키 발급")
	void signin_setsCookie() throws Exception {
		var user = User.of("a@b.com", new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("pass"), "닉", "KR");
		userRepository.save(user);

		var dto = new JwtSigninRequestDto("a@b.com", "pass");

		mockMvc.perform(post("/api/user/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isOk())
			.andExpect(header().string("Set-Cookie", containsString("accessToken")));
	}

	@Test
	@DisplayName("내 정보 조회 - 인증 성공")
	void me_authenticated() throws Exception {
		var user = User.of("x@y.com", new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("pw"), "닉", "KR");
		userRepository.save(user);

		String token = jwtUtil.generateToken(user.getId());

		mockMvc.perform(get("/api/user/me")
				.cookie(new Cookie("accessToken", token)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("x@y.com"));
	}

	@Test
	@DisplayName("로그아웃 시 쿠키 만료")
	void signout_expiresCookie() throws Exception {
		mockMvc.perform(post("/api/user/signout"))
			.andExpect(status().isOk())
			.andExpect(header().string("Set-Cookie", containsString("Max-Age=0")));
	}
}
