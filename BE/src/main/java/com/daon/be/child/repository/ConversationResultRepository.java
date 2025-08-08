package com.daon.be.child.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import com.daon.be.conversation.entity.ConversationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConversationResultRepository extends JpaRepository<ConversationResult, Long> {

	// 아이별로 오늘 저장됐는지(서비스 레벨 검사용)
	boolean existsByChildIdAndCreatedAtBetween(
		Long childId, LocalDateTime start, LocalDateTime end
	);

	// 아이별로 오늘 첫 저장 레코드
	Optional<ConversationResult> findTopByChildIdAndCreatedAtBetweenOrderByCreatedAtAsc(
		Long childId, LocalDateTime start, LocalDateTime end
	);

	// 아이+캘린더(하루) 기준 조회/검사 — DB에 (child_id, calendar_id) UNIQUE를 둘 경우 동시성 방어에 유용
	boolean existsByChildIdAndCalendarId(Long childId, Long calendarId);
	Optional<ConversationResult> findByChildIdAndCalendarId(Long childId, Long calendarId);

	// 날짜 기반으로 바로 찾고 싶을 때(옵션)
	@Query("""
        select cr
        from ConversationResult cr
        join cr.calendar c
        where cr.child.id = :childId
          and c.date = :date
        order by cr.createdAt asc
    """)
	Optional<ConversationResult> findFirstByChildIdAndDate(
		@Param("childId") Long childId,
		@Param("date") LocalDate date
	);
}
