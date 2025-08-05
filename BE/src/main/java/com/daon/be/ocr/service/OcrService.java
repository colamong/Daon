package com.daon.be.ocr.service;

import com.daon.be.ai.client.OpenAiOcrClient;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OcrService {

    private final OpenAiOcrClient openAiOcrClient;

    /**
     * OCR로 이미지에서 텍스트 추출
     */
    public String extractTextFromImage(File imageFile) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata"); // 경로는 환경에 맞게
        tesseract.setLanguage("kor+eng");
        return tesseract.doOCR(imageFile);
    }

    /**
     * 전체 프로세스: OCR → 쉬운 한국어 요약 → 모국어 번역
     */
    public Map<String, String> processOcrWithTranslation(File imageFile, String nativeLang) throws TesseractException {
        Map<String, String> result = new HashMap<>();

        // 1. OCR
        String ocrText = extractTextFromImage(imageFile);
        result.put("ocrText", ocrText);

        // 2. 쉬운 한국어 요약
        // 앞서 추출한 ocrText를 GPT 모델에게 보내 쉬운 한국어로 요약 받습니다.
        // 내부적으로 OpenAiOcrClient에서 prompt와 system 메시지를 세팅해서 요청합니다.
        String summary = openAiOcrClient.summarizeToSimpleKorean(ocrText);
        result.put("summary", summary);

        // 3. 모국어로 번역
        String translated = openAiOcrClient.translateToNativeLanguage(summary, nativeLang);
        result.put("translated", translated);

        return result;
    }
}
