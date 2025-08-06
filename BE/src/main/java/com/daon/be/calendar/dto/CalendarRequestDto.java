package com.daon.be.calendar.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CalendarRequestDto {

	@NotNull(message = "사용자 ID는 필수")
	private Long userId;

	@NotNull(message = "일정 날짜는 필수")
	private LocalDate eventDate;

	@NotNull(message = "일정 제목은 필수")
	private String title;

	private String description;

	public CalendarRequestDto(Long userId, LocalDate eventDate, String title, String description) {
		this.userId = userId;
		this.eventDate = eventDate;
		this.title = title;
		this.description = description;
	}
}
