package com.daon.be.learning.repository;

import com.daon.be.learning.entity.Chapter;
import com.daon.be.learning.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter,Long> {
    List<Chapter> findAllByTheme(Theme theme);
    
    // 추가: 테마별 챕터 번호 순으로 정렬
    List<Chapter> findAllByThemeOrderByChapterNumber(Theme theme);
}
