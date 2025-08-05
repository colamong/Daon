package com.daon.be.calendar.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daon.be.calendar.entity.Calendar;
import com.daon.be.conversation.entity.ChildAnswer;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
	Optional<Calendar> findByChildIdAndDate(Long childId, LocalDate date);

	Calendar save(Calendar newCalendar);
}
