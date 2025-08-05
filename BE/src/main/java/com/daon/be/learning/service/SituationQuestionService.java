package com.daon.be.learning.service;

import com.daon.be.learning.dto.response.ChapterResponseDTO;
import com.daon.be.learning.dto.response.ChoiceResponseDTO;
import com.daon.be.learning.dto.response.QuestionResponseDTO;
import com.daon.be.learning.entity.Chapter;
import com.daon.be.learning.entity.SituationChoice;
import com.daon.be.learning.entity.SituationQuestion;
import com.daon.be.learning.repository.ChapterRepository;
import com.daon.be.learning.repository.SituationChoiceRepository;
import com.daon.be.learning.repository.SituationQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SituationQuestionService {

    private final ChapterRepository chapterRepository;
    private final SituationQuestionRepository questionRepository;
    private final SituationChoiceRepository choiceRepository;

    public List<QuestionResponseDTO> getQuestionsByChapterId(Long chapterId){
        // 챕터 먼저 조회
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 챕터"));

        // 해당 챕터의 질문 리스트 조회
        List<SituationQuestion> questions = questionRepository.findAllByChapter(chapter);


        // 각 질문에 대한 보기 리스트 조회 및 DTO 조립
        return questions.stream()
                .map(question -> {
                    List<SituationChoice> choices = choiceRepository.findAllByQuestion(question);

                    List<ChoiceResponseDTO> choiceDTOs = choices.stream()
                            .map(choice -> ChoiceResponseDTO.builder()
                                    .id(choice.getId())
                                    .choiceText(choice.getChoiceText())
                                    .isCorrect(choice.isCorrect())
                                    .audioUrl(choice.getAudioUrl())
                                    .build())
                            .collect(Collectors.toList());

                    return QuestionResponseDTO.builder()
                            .id(question.getId())
                            .questionText(question.getQuestionText())
                            .audioUrl(question.getAudioUrl())
                            .choices(choiceDTOs)
                            .build();
                })
                .collect(Collectors.toList());


    }


}
