package com.daon.be.user.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Component
public class JwtUtil {

	private final String SECRET = "your-secret-key";
	private final long EXPIRATION_MS = 1000 * 60 * 60 * 8; // 8시간

	private final Algorithm algorithm = Algorithm.HMAC256(SECRET);

	public String generateToken(Long userId) {
		return JWT.create()
			.withSubject(String.valueOf(userId))
			.withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_MS))
			.sign(algorithm);
	}

	public Long validateAndExtractUserId(String token) {
		try {
			String subject = JWT.require(algorithm)
				.build()
				.verify(token)
				.getSubject();

			return Long.parseLong(subject);
		} catch (JWTVerificationException e) {
			return null; // 유효하지 않은 토큰
		}
	}
}
