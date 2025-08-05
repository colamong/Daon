package com.daon.be.learning.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class QuestionResponseDTO {
    private final Long id;
    private final String questionText;
    private final String audioUrl;
    private final List<ChoiceResponseDTO> choices;

    @Builder
    public QuestionResponseDTO(Long id,String questionText,String audioUrl, List<ChoiceResponseDTO> choices){
        this.id = id;
        this.questionText = questionText;
        this.audioUrl =  audioUrl;
        this.choices = choices;
    }
}
