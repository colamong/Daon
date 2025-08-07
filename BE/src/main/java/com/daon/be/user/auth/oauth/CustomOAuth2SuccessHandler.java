package com.daon.be.user.auth.oauth;

import com.daon.be.user.entity.User;
import com.daon.be.user.repository.UserRepository;
import com.daon.be.user.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;

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
		Cookie cookie = new Cookie("accessToken", token);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60 * 8); // 8시간

		response.addCookie(cookie);

		// json응답
		//리다이렉트로 변경하고 싶으면
		//response.sendRedirect("/login-success");
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().write("""
			    {
			      "message": "OAuth2 로그인 성공",
			      "user": {
			        "id": %d,
			        "email": "%s",
			        "nickname": "%s"
			      }
			    }
			""".formatted(user.getId(), user.getEmail(), user.getNickname()));
	}
}
