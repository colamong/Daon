package com.daon.be.learning.repository;

import com.daon.be.learning.entity.Chapter;
import com.daon.be.learning.entity.SituationQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SituationQuestionRepository extends JpaRepository<SituationQuestion,Long> {
    List<SituationQuestion> findAllByChapter(Chapter chapter);
}
