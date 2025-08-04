package com.daon.be.learning.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ThemeResponseDTO {

    private final Long id;
    private final String name;
    private final String description;

    @Builder
    public ThemeResponseDTO(Long id, String name, String description){
        this.id = id;
        this.name =name;
        this.description = description;
    }
}
