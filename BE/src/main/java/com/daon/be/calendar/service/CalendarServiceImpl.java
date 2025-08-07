package com.daon.be.calendar.service;

import com.daon.be.calendar.dto.CalendarRequestDto;
import com.daon.be.calendar.dto.CalendarUpdateRequestDto;
import com.daon.be.calendar.dto.CalendarResponseDto;
import com.daon.be.calendar.entity.CalendarEvent;
import com.daon.be.calendar.repository.CalendarEventRepository;
import com.daon.be.global.exception.BusinessException;
import com.daon.be.global.exception.ErrorCode;
import com.daon.be.user.entity.User;
import com.daon.be.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

	private final CalendarEventRepository calendarEventRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public CalendarResponseDto create(Long userId, CalendarRequestDto dto) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.CALENDAR_UNAUTHORIZED, "존재하지 않는 사용자입니다."));
		CalendarEvent event = CalendarEvent.of(user, dto);
		CalendarEvent saved = calendarEventRepository.save(event);
		return CalendarResponseDto.from(saved);
	}


	@Override
	@Transactional
	public CalendarResponseDto getById(Long id, Long userId) {
		CalendarEvent event = calendarEventRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.CALENDAR_NOT_FOUND, "존재하지 않는 일정입니다."));
		if (!event.getUser().getId().equals(userId)) {
			throw new BusinessException(ErrorCode.CALENDAR_UNAUTHORIZED, "해당 일정에 대한 권한이 없습니다.");
		}
		return CalendarResponseDto.from(event);
	}

	@Override
	public List<CalendarResponseDto> getByUserAndDate(Long userId, LocalDate date) {
		List<CalendarEvent> events;
		if (date == null) {
			events = calendarEventRepository.findByUserId(userId);
		} else {
			events = calendarEventRepository.findByUserIdAndEventDate(userId, date);
		}
		return events.stream()
			.map(CalendarResponseDto::from)
			.collect(Collectors.toList());
	}

	@Override
	public List<CalendarResponseDto> getMonthlyEvents(Long userId, int year, int month) {
		LocalDate start = LocalDate.of(year, month, 1);
		LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
		List<CalendarEvent> events = calendarEventRepository.findByUserIdAndEventDateBetween(userId, start, end);
		return events.stream()
			.map(CalendarResponseDto::from)
			.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public CalendarResponseDto update(Long id, Long userId, CalendarUpdateRequestDto dto) {
		CalendarEvent event = calendarEventRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.CALENDAR_NOT_FOUND, "존재하지 않는 일정입니다."));
		if (!event.getUser().getId().equals(userId)) {
			throw new BusinessException(ErrorCode.CALENDAR_UNAUTHORIZED, "해당 일정에 대한 권한이 없습니다.");
		}
		event.updateFrom(dto);
		return CalendarResponseDto.from(event);
	}

	@Override
	@Transactional
	public void delete(Long id, Long userId) {
		CalendarEvent event = calendarEventRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.CALENDAR_NOT_FOUND, "존재하지 않는 일정입니다."));
		if (!event.getUser().getId().equals(userId)) {
			throw new BusinessException(ErrorCode.CALENDAR_UNAUTHORIZED, "해당 일정에 대한 권한이 없습니다.");
		}
		calendarEventRepository.deleteById(id);
	}
}