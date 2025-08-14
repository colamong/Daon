package com.daon.be.learning.service;

import com.daon.be.learning.dto.response.ChapterResponseDTO;
import com.daon.be.learning.entity.Theme;
import com.daon.be.learning.repository.ChapterRepository;
import com.daon.be.learning.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final ThemeRepository themeRepository;

    public List<ChapterResponseDTO> getChaptersByThemeId(Long themeId){
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 테마입니다" + themeId));

        return chapterRepository.findAllByThemeOrderByChapterNumber(theme).stream() // 정렬 추가
                .map(chapter -> ChapterResponseDTO.builder()
                        .id(chapter.getId())
                        .chapterNumber(chapter.getChapterNumber()) // 챕터 번호 추가
                        .title(chapter.getTitle())
                        .description(chapter.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
