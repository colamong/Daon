package com.daon.be.calendar.repository;

import com.daon.be.calendar.entity.ImageDiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ImageDiaryRepository extends JpaRepository<ImageDiary, Long> {

    Optional<ImageDiary> findByCalendar_Id(Long calendarId);

    List<ImageDiary> findAllByCalendar_Child_IdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long childId, LocalDateTime start, LocalDateTime end
    );
}
