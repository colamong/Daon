package com.daon.be.ai.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiOcrClient {

    private final RestTemplate restTemplate; // GPT API 서버에 HTTP 요청 보내기 위한 Spring 기본 HTTP 클라이언트

    @Value("${gms.api.key}")
    private String gmsApiKey; // GPT 서버 호출 시 사용하는 인증 토큰

    @Value("${gms.api.url}")
    private String gmsApiUrl; // GPT 서버의 URL


    // OCR 결과를 쉬운 한국어로 요약
    public String summarizeToSimpleKorean(String ocrText) {
        String prompt = String.format("다음 OCR 내용을 쉬운 한국어로 요약해줘:\n%s", ocrText);
        String system = "너는 한국어 요약을 잘하는 분석가야. 문장을 짧고 쉽게 만들어줘. 대신 '\n' 은 따로 쓰지말고 만들어 줄래?";
        return requestGptWithPrompt(prompt, system);
    }


    // 요약된 한국어를 사용자 모국어로 번역
    public String translateToNativeLanguage(String koreanText, String nativeLanguage) {
        String prompt = String.format("다음 내용을 '%s'로 자연스럽게 번역해줘:\n%s", nativeLanguage, koreanText);
        String system = "너는 친절한 번역가야. 한국어를 다른 언어로 자연스럽게 바꿔줘.";
        return requestGptWithPrompt(prompt, system);
    }


    //공통 GPT 요청 처리 메서드 (JsonNode로 content 파싱)
    private String requestGptWithPrompt(String userPrompt, String systemInstruction) {
        try {
            Map<String, Object> userMsg = Map.of("role", "user", "content", userPrompt);
            Map<String, Object> systemMsg = Map.of("role", "system", "content", systemInstruction);

            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4.1-nano",
                    "messages", List.of(systemMsg, userMsg),
                    "max_tokens", 1024,
                    "temperature", 0.3
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + gmsApiKey);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            log.info("GMS OCR 요청 바디: {}", jsonBody);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    gmsApiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String responseBody = response.getBody();
            log.info("GMS OCR 응답 결과: {}", responseBody);

            // content만 파싱해서 반환
            JsonNode root = objectMapper.readTree(responseBody);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            return content;

        } catch (HttpClientErrorException e) {
            log.error("GMS OCR 요청 실패: {}", e.getResponseBodyAsString());
            return "GMS 오류: " + e.getResponseBodyAsString();
        } catch (Exception e) {
            log.error("GMS OCR 예외", e);
            return "GMS 예외: " + e.getMessage();
        }
    }
}
