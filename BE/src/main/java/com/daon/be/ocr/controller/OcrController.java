package com.daon.be.ocr.controller;

import com.daon.be.global.common.LanguageMapper;
import com.daon.be.ocr.service.OcrService;
import com.daon.be.user.auth.SigninUser;
import com.daon.be.user.entity.User;
import com.daon.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
@Slf4j
public class OcrController {

    private final OcrService ocrService;
    private final UserRepository userRepository;

    @PostMapping("/extract")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("image") MultipartFile image,
            @SigninUser Long userId) throws IOException, TesseractException {
        
        // 업로드된 이미지 파일을 서버의 임시 디렉토리에 저장
        File tempFile = File.createTempFile("upload", image.getOriginalFilename());
        image.transferTo(tempFile);

        
        // 사용자 정보에서 국가코드를 가져와서 언어명으로 변환
        String userNativeLanguage = "English"; // 기본값
        if (userId != null) {
            try {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
                userNativeLanguage = LanguageMapper.getLanguageFromNationCode(user.getNationCode());
                log.info("OCR 요청 - 사용자 ID: {}, 국가코드: {}, 언어: {}", userId, user.getNationCode(), userNativeLanguage);
            } catch (Exception e) {
                log.warn("사용자 정보 조회 실패, 기본 언어(English) 사용: {}", e.getMessage());
            }
        } else {
            log.warn("로그인되지 않은 사용자, 기본 언어(English) 사용");
        }

        //Tess4J로 이미지에서 텍스트 추출
        //OpenAiOcrClient.summarizeToSimpleKorean() 호출
        //OpenAiOcrClient.translateToNativeLanguage() 호출
        Map<String, String> result = ocrService.processOcrWithTranslation(tempFile, userNativeLanguage);

        return ResponseEntity.ok(result);
    }
}
