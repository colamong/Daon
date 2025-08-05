package com.daon.be.learning.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChoiceResponseDTO {
    private final Long id;
    private final String choiceText;
    private final boolean isCorrect;
    private final String audioUrl;

    @Builder
    public ChoiceResponseDTO(Long id,String choiceText,String audioUrl,boolean isCorrect){
        this.id = id;
        this.choiceText = choiceText;
        this.audioUrl = audioUrl;
        this.isCorrect = isCorrect;
    }

}
