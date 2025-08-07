package com.daon.be.user.auth;

import com.daon.be.user.auth.SigninUser;
import com.daon.be.user.auth.SigninUser;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class SigninUserArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(SigninUser.class)
			&& parameter.getParameterType().equals(Long.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {

		HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
		return request.getAttribute("userId");
	}
}
