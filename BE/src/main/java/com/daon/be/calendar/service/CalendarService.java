package com.daon.be.calendar.service;

import java.time.LocalDate;
import java.util.List;

import com.daon.be.calendar.dto.CalendarRequestDto;
import com.daon.be.calendar.dto.CalendarResponseDto;
import com.daon.be.calendar.dto.CalendarUpdateRequestDto;

public interface CalendarService {

	// 일정 추가
	CalendarResponseDto create(Long userId, CalendarRequestDto requestDto);

	CalendarResponseDto getById(Long id, Long userId);

	List<CalendarResponseDto> getByUserAndDate(Long userId, LocalDate date); // date null이면 전체

	List<CalendarResponseDto> getMonthlyEvents(Long userId, int year, int month);

	CalendarResponseDto update(Long id, Long userId, CalendarUpdateRequestDto dto);

	void delete(Long id, Long userId);
}
