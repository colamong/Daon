package com.daon.be.learning.repository;

import com.daon.be.learning.entity.SituationChoice;
import com.daon.be.learning.entity.SituationQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SituationChoiceRepository extends JpaRepository<SituationChoice,Long> {
    List<SituationChoice> findAllByQuestion(SituationQuestion question);
}
