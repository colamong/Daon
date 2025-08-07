package com.daon.be.ocr.service;

import com.daon.be.ai.client.OpenAiOcrClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OcrService {

    private final OpenAiOcrClient openAiOcrClient;

    @Value("${tesseract.path}")
    private String tesseractPath;

    public String extractTextFromImage(File imageFile) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tesseractPath); // ← 환경 설정 값 주입
        tesseract.setLanguage("kor+eng");
        return tesseract.doOCR(imageFile);
    }

    public Map<String, String> processOcrWithTranslation(File imageFile, String nativeLang) throws TesseractException {
        Map<String, String> result = new HashMap<>();

        String ocrText = extractTextFromImage(imageFile);
        result.put("ocrText", ocrText);

        String summary = openAiOcrClient.summarizeToSimpleKorean(ocrText);
        result.put("summary", summary);

        String translated = openAiOcrClient.translateToNativeLanguage(summary, nativeLang);
        result.put("translated", translated);

        return result;
    }
}
