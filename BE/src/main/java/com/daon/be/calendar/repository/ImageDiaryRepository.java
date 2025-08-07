package com.daon.be.calendar.repository;

import com.daon.be.calendar.entity.ImageDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ImageDiaryRepository extends JpaRepository<ImageDiary, Long> {

    @Query("SELECT d FROM ImageDiary d JOIN FETCH d.calendar c JOIN FETCH c.child WHERE d.id = :id")
    Optional<ImageDiary> findWithCalendarAndChildById(@Param("id") Long id);

    @Query("""
    SELECT d FROM ImageDiary d
    JOIN FETCH d.calendar c
    JOIN FETCH c.child
    WHERE c.child.id = :childId
      AND d.createdAt BETWEEN :start AND :end
    ORDER BY d.createdAt DESC
""")
    List<ImageDiary> findWithCalendarAndChildByChildIdAndCreatedAtBetweenOrderByCreatedAtDesc(
        @Param("childId") Long childId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

}
