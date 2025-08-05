package com.daon.be.learning.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChapterResponseDTO {

    private final Long id;
    private final String title;
    private final String description;

    @Builder
    public ChapterResponseDTO(Long id,String title,String description){
        this.id = id;
        this.title = title;
        this.description = description;
    }
}
