package com.daon.be.user.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

	private final JwtUtil jwtUtil;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String token = extractTokenFromCookie(request);

		if (token == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}

		Long userId = jwtUtil.validateAndExtractUserId(token);
		if (userId == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}

		request.setAttribute("userId", userId);
		return true;
	}

	private String extractTokenFromCookie(HttpServletRequest request) {
		if (request.getCookies() == null) return null;

		for (Cookie cookie : request.getCookies()) {
			if (cookie.getName().equals("accessToken")) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
