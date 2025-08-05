package com.daon.be.learning.controller;

import com.daon.be.learning.dto.response.QuestionResponseDTO;
import com.daon.be.learning.service.SituationQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study/chapters")
public class SituationQuestionController {
    private final SituationQuestionService situationQuestionService;

    @GetMapping("/{chapterId}/questions")
    public List<QuestionResponseDTO> getQuestionsByChapterId(@PathVariable Long chapterId){
        return situationQuestionService.getQuestionsByChapterId(chapterId);
    }
}
