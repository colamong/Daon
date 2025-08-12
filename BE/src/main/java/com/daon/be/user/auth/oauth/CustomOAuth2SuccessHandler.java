package com.daon.be.user.auth.oauth;

import com.daon.be.user.entity.User;
import com.daon.be.user.repository.UserRepository;
import com.daon.be.user.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;

	@Value("${frontend.origin}")
	private String frontendOrigin;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();

		String email = oAuth2User.getAttribute("email");
		String nickname = oAuth2User.getAttribute("name"); // Google 계정 이름
		String provider = "GOOGLE";
		if (userRepository.existsByEmailAndProvider(email, User.Provider.LOCAL)) {
			throw new IllegalArgumentException("해당 이메일은 일반 가입으로 이미 사용 중입니다.");
		}

		User user = userRepository.findByEmailAndProvider(email, User.Provider.GOOGLE)
			.orElseGet(() -> {
				User newUser = new User();
				newUser.setEmail(email);
				newUser.setNickname(nickname);
				newUser.setPassword("");
				newUser.setProvider(User.Provider.GOOGLE);
				newUser.setNationCode("KR");
				return userRepository.save(newUser);
			});


		// JWT 생성
		String token = jwtUtil.generateToken(user.getId());

		// JWT를 쿠키로 저장
		ResponseCookie c = ResponseCookie.from("accessToken", token)
			.httpOnly(true)
			.path("/")
			.maxAge(Duration.ofHours(8))
			.sameSite("Lax")
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, c.toString());

		// 혹시 모를 SavedRequest 삭제(다른 리다이렉트에 덮이지 않게)
		HttpSession session = request.getSession(false);
		if (session != null) session.removeAttribute("SPRING_SECURITY_SAVED_REQUEST");

		// 절대경로로 302 지정 후 즉시 종료
		String target = frontendOrigin.endsWith("/") ? frontendOrigin : (frontendOrigin + "/dashboard");
		System.out.println("[OAuth2Success] redirect -> " + target);
		response.setStatus(HttpServletResponse.SC_FOUND);
		response.setHeader(HttpHeaders.LOCATION, target);
		return;
	}


}
