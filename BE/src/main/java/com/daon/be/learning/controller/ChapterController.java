package com.daon.be.learning.controller;

import com.daon.be.learning.dto.response.ChapterResponseDTO;
import com.daon.be.learning.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study/themes")
public class ChapterController {
    private final ChapterService chapterService;

    @GetMapping("/{themeId}/chapters")
    public ResponseEntity<List<ChapterResponseDTO>> getChapterByThemeId(@PathVariable Long themeId){
        List<ChapterResponseDTO> chapters = chapterService.getChaptersByThemeId(themeId);
        return ResponseEntity.ok(chapters);
    }

}
