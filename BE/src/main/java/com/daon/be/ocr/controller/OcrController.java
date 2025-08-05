package com.daon.be.ocr.controller;

import com.daon.be.ocr.service.OcrService;
import lombok.RequiredArgsConstructor;
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
public class OcrController {

    private final OcrService ocrService;

    @PostMapping("/extract")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile image)
            throws IOException, TesseractException {
        // 업로드된 이미지 파일을 서버의 임시 디렉토리에 저장
        File tempFile = File.createTempFile("upload", image.getOriginalFilename());
        image.transferTo(tempFile);

        // 💡 실제로는 로그인 유저 정보에서 가져와야 함
        String userNativeLanguage = "Vietnamese";  // ← 더미 데이터 (나중에 연동)

        //Tess4J로 이미지에서 텍스트 추출
        //OpenAiOcrClient.summarizeToSimpleKorean() 호출
        //OpenAiOcrClient.translateToNativeLanguage() 호출
        Map<String, String> result = ocrService.processOcrWithTranslation(tempFile, userNativeLanguage);

        return ResponseEntity.ok(result);
    }
}
