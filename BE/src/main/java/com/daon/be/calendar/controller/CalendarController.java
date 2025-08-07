package com.daon.be.calendar.controller;

import com.daon.be.calendar.dto.CalendarRequestDto;
import com.daon.be.calendar.dto.CalendarUpdateRequestDto;
import com.daon.be.calendar.dto.CalendarResponseDto;
import com.daon.be.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {

	private final CalendarService calendarService;

	// 일정 생성
	@PostMapping("/{userId}/events")
	public ResponseEntity<CalendarResponseDto> createEvent(
		@PathVariable Long userId,
		@Valid @RequestBody CalendarRequestDto requestDto) {
		CalendarResponseDto response = calendarService.create(userId, requestDto);
		return ResponseEntity.ok(response);
	}

	// 전체/날짜별 조회
	@GetMapping("/{userId}/events")
	public ResponseEntity<List<CalendarResponseDto>> getEventsByUserAndDate(
		@PathVariable Long userId,
		@RequestParam(required = false) LocalDate date) {
		return ResponseEntity.ok(calendarService.getByUserAndDate(userId, date));
	}

	// 단건 조회
	@GetMapping("/{userId}/events/{id}")
	public ResponseEntity<CalendarResponseDto> getEvent(
		@PathVariable Long userId,
		@PathVariable Long id) {
		return ResponseEntity.ok(calendarService.getById(id, userId));
	}

	// 월별 조회
	@GetMapping("/{userId}/events/monthly")
	public ResponseEntity<List<CalendarResponseDto>> getMonthlyEvents(
		@PathVariable Long userId,
		@RequestParam int year,
		@RequestParam int month) {
		return ResponseEntity.ok(calendarService.getMonthlyEvents(userId, year, month));
	}

	@PutMapping("/{userId}/events/{id}")
	public ResponseEntity<CalendarResponseDto> updateEvent(
		@PathVariable Long userId,
		@PathVariable Long id,
		@Valid @RequestBody CalendarUpdateRequestDto updateDto) {
		CalendarResponseDto response = calendarService.update(id, userId, updateDto);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{userId}/events/{id}")
	public ResponseEntity<Void> deleteEvent(
		@PathVariable Long userId,
		@PathVariable Long id) {
		calendarService.delete(id, userId);
		return ResponseEntity.ok().build();
	}
}
