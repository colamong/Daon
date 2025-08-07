	package com.daon.be.global.config;


	import java.util.List;

	import com.daon.be.user.auth.SigninUserArgumentResolver;

	import lombok.RequiredArgsConstructor;

	import org.springframework.beans.factory.annotation.Value;
	import org.springframework.context.annotation.Configuration;
	import org.springframework.web.method.support.HandlerMethodArgumentResolver;
	import org.springframework.web.servlet.config.annotation.CorsRegistry;
	import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

	@Configuration
	@RequiredArgsConstructor
	public class WebConfig implements WebMvcConfigurer {

		private final SigninUserArgumentResolver signinUserArgumentResolver;

		@Value("${frontend.origin}")
		private String frontendOrigin;

		@Override
		public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
			resolvers.add(signinUserArgumentResolver);
		}

		@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/**")
				.allowedOrigins(frontendOrigin)
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowCredentials(true)
				.allowedHeaders("*");
		}
	}
