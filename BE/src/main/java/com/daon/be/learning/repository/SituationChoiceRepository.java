package com.daon.be.learning.repository;

import com.daon.be.learning.entity.SituationChoice;
import com.daon.be.learning.entity.SituationQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SituationChoiceRepository extends JpaRepository<SituationChoice,Long> {

    List<SituationChoice> findAllByQuestion(SituationQuestion question);

    // questionId와 isCorrect=true 조건을 만족하는 정답 보기 하나 조회
    Optional<SituationChoice> findByQuestionIdAndIsCorrectTrue(Long questionId);
}
