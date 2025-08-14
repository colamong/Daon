package com.daon.be.global.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// 비즈니스 예외 처리 (권한, 찾을 수 없음 등)
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorCode> handleBusinessException(BusinessException ex) {
		log.warn("BusinessException 발생: {}", ex.getMessage()); // 경고 로그
		ErrorCode errorCode = ex.getErrorCode();
		return ResponseEntity
				.status(errorCode.getStatus())
				.body(errorCode);
	}

	// 그 외 모든 예외 (서버에러)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorCode> handleAllExceptions(Exception ex) {
		// 스택트레이스까지 콘솔에 출력
		log.error("Unhandled Exception 발생", ex);

		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR; // enum에 반드시 정의
		return ResponseEntity
				.status(errorCode.getStatus())
				.body(errorCode);
	}
}
