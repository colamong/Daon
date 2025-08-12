package com.daon.be.global.config;

import java.util.List;

import com.daon.be.user.auth.oauth.CustomOAuth2FailureHandler;
import com.daon.be.user.auth.oauth.CustomOAuth2SuccessHandler;
import com.daon.be.user.jwt.JwtAuthenticationFilter;
import com.daon.be.user.jwt.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomOAuth2SuccessHandler oAuth2SuccessHandler;
	private final CustomOAuth2FailureHandler oAuth2FailureHandler;
	private final JwtUtil jwtUtil;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.cors(Customizer.withDefaults()) // ← 추가 (나머지는 그대로)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/**").permitAll()
						.anyRequest().authenticated()
				)
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((request, response, authException) -> {
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
						})
				)
				.oauth2Login(oauth2 -> oauth2
						.successHandler(oAuth2SuccessHandler)
						.failureHandler(oAuth2FailureHandler)
				)
				.addFilterBefore(
						new JwtAuthenticationFilter(jwtUtil),
						UsernamePasswordAuthenticationFilter.class
				);

		return http.build();
	}

	// ★ 추가: CORS 전역 설정 (진단 단계: 패턴 * 로 개방)
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		// 로컬 안 깨지게 우선 전부 허용(패턴) → 문제 해결 후 정확한 도메인으로 좁힐 것
		config.setAllowedOriginPatterns(List.of("*"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
		config.setExposedHeaders(List.of("Authorization"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
