package com.daon.be.calendar.repository;

import com.daon.be.calendar.entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

	// 지정한 userId의 eventDate에 등록된 모든 일정을 조회
	List<CalendarEvent> findByUserIdAndEventDate(Long userId, LocalDate eventDate);

	List<CalendarEvent> findByUserId(Long userId);

	List<CalendarEvent> findByUserIdAndEventDateBetween(Long userId, LocalDate start, LocalDate end);

}
