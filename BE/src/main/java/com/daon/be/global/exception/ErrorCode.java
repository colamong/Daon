package com.daon.be.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

	CALENDAR_UNAUTHORIZED(HttpStatus.FORBIDDEN, 4401, "해당 일정에 대한 권한이 없습니다."),
	CALENDAR_NOT_FOUND(HttpStatus.NOT_FOUND, 4402, "해당 일정을 찾을 수 없습니다."),

	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500 ,"서버 오류가 발생했습니다.");

	private final HttpStatus status;
	private final int code;
	private final String message;

	ErrorCode(HttpStatus status, int code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}



