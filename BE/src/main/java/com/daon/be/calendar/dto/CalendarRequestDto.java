package com.daon.be.calendar.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CalendarRequestDto {

	@NotNull(message = "일정 날짜는 필수")
	private LocalDate eventDate;

	@NotNull(message = "일정 제목은 필수")
	private String title;

	private String description;
}
