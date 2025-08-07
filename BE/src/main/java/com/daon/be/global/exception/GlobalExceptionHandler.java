package com.daon.be.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// 비즈니스 예외 처리 (권한, 찾을 수 없음 등)
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorCode> handleBusinessException(BusinessException ex) {
		ErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(errorCode);
	}

	// 그 외 모든 예외 (서버에러)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorCode> handleAllExceptions(Exception ex) {
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR; // enum에 반드시 정의
		return ResponseEntity
			.status(errorCode.getStatus())
			.body(errorCode);
	}
}
