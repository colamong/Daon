package com.daon.be.global.config;

import com.daon.be.user.auth.oauth.CustomOAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomOAuth2SuccessHandler oAuth2SuccessHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.cors(cors -> {}) // CORS 설정은 WebConfig에서 함
			.authorizeHttpRequests(auth -> auth
					.anyRequest().permitAll()
				/*.requestMatchers(
					"/api/user/signup",
					"/api/user/signin",
					"/api/user/nations",
					"/api/user/nation/**",
					"/oauth2/**",
					"/login/**"
				).permitAll()
				.anyRequest().authenticated()*/
			)
			.oauth2Login(oauth2 -> oauth2
				.successHandler(oAuth2SuccessHandler)
			);

		return http.build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
