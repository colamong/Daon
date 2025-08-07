package com.daon.be.global.config;

import com.daon.be.user.auth.oauth.CustomOAuth2SuccessHandler;
import com.daon.be.user.jwt.JwtAuthenticationFilter;
import com.daon.be.user.jwt.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomOAuth2SuccessHandler oAuth2SuccessHandler;
	private final JwtUtil jwtUtil;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.cors(cors -> {}) // CORS 설정은 WebConfig에서 함
			.authorizeHttpRequests(auth -> auth
					//.anyRequest().permitAll()
				.requestMatchers(
					"/api/user/signup",
					"/api/user/signin",
					"/api/user/nations",
					"/api/user/nation/**",
					"/oauth2/**",
					"/login/**"
				).permitAll()
				.anyRequest().authenticated()
			)
			.exceptionHandling(ex -> ex
				.authenticationEntryPoint((request, response, authException) -> {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
				})
			)
			.oauth2Login(oauth2 -> oauth2
				.successHandler(oAuth2SuccessHandler)
			)
			// JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
			.addFilterBefore(
				new JwtAuthenticationFilter(jwtUtil),
				UsernamePasswordAuthenticationFilter.class
			);

		return http.build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
