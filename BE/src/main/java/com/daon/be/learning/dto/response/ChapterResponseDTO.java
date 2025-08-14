package com.daon.be.learning.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChapterResponseDTO {

    private final Long id;
    private final Integer chapterNumber; // 추가: 테마별 챕터 번호
    private final String title;
    private final String description;

    @Builder
    public ChapterResponseDTO(Long id, Integer chapterNumber, String title, String description){
        this.id = id;
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.description = description;
    }
}
