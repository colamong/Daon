package com.daon.be.calendar.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CalendarUpdateRequestDto {

	@NotNull(message = "일정 날짜는 필수입니다.")
	private LocalDate eventDate;

	@NotNull(message = "일정 제목은 필수입니다.")
	private String title;

	private String description;
}