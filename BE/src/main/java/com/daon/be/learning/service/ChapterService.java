package com.daon.be.learning.service;

import com.daon.be.learning.dto.response.ChapterResponseDTO;
import com.daon.be.learning.entity.Chapter;
import com.daon.be.learning.entity.Theme;
import com.daon.be.learning.repository.ChapterRepository;
import com.daon.be.learning.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.Table;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final ThemeRepository themeRepository;

    public List<ChapterResponseDTO> getChaptersByThemeId(Long themeId){
        try {
            Theme theme = themeRepository.findById(themeId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 테마입니다" + themeId));

            System.out.println("=== 디버깅 시작 ===");
            System.out.println("테마 ID: " + themeId);
            System.out.println("테마 이름: " + theme.getName());
            
            // 임시로 기존 메서드 사용
            List<Chapter> chapters = chapterRepository.findAllByTheme(theme);
            System.out.println("챕터 개수: " + chapters.size());
            
            // 각 챕터 정보 출력
            for (Chapter chapter : chapters) {
                System.out.println("챕터 ID: " + chapter.getId() + 
                                 ", 테마 ID: " + chapter.getTheme().getId() + 
                                 ", 챕터 번호: " + chapter.getChapterNumber() + 
                                 ", 제목: " + chapter.getTitle());
            }
            
            // DTO 변환
            List<ChapterResponseDTO> result = chapters.stream()
                    .map(chapter -> {
                        ChapterResponseDTO dto = ChapterResponseDTO.builder()
                                .id(chapter.getId())
                                .chapterNumber(chapter.getChapterNumber())
                                .title(chapter.getTitle())
                                .description(chapter.getDescription())
                                .build();
                        
                        System.out.println("생성된 DTO: " + dto);
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            System.out.println("=== 디버깅 완료 ===");
            return result;
            
        } catch (Exception e) {
            System.err.println("=== 에러 발생 ===");
            System.err.println("에러 메시지: " + e.getMessage());
            e.printStackTrace();
            System.err.println("==================");
            throw e;
        }
    }
}
