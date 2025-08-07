package com.daon.be.calendar.dto;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import com.daon.be.calendar.entity.CalendarEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CalendarResponseDto {

	private Long calendarId;
	private Long userId;
	private LocalDate eventDate;
	private String dayOfWeek;
	private String title;
	private String description;

	public static CalendarResponseDto from(CalendarEvent entity) {
		return new CalendarResponseDto(
			entity.getId(),
			entity.getUser().getId(),
			entity.getEventDate(),
			entity.getEventDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN),
			entity.getTitle(),
			entity.getDescription()
		);
	}
}
