package com.daon.be.user.auth.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

	@Value("${frontend.origin:http://localhost:5173}")
	private String frontendOrigin;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException {
		String msg = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
		String target = (frontendOrigin.endsWith("/") ? frontendOrigin : frontendOrigin + "/") + "login?error=" + msg;

		response.setStatus(HttpServletResponse.SC_FOUND); // 302
		response.setHeader(HttpHeaders.LOCATION, target);
	}
}
