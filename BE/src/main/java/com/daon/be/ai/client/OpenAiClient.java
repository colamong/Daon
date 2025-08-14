package com.daon.be.ai.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient {

	private final RestTemplate restTemplate;

	@Value("${gms.api.key}")
	private String gmsApiKey;

	@Value("${gms.api.url}")
	private String gmsApiUrl;

	public String requestGpt(String prompt) {
		try {
			Map<String, Object> userMsg = Map.of("role", "user", "content", prompt);
			Map<String, Object> systemMsg = Map.of("role", "system", "content", "감정 분석과 대화 전체를 요약하는 분석가 역할이야. 최소 200자에서 최대 300자로 말하고 친절하게 -습니다 말투를 써야 돼.");

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
			String jsonBody = objectMapper.writeValueAsString(requestBody); // 💡 안전한 JSON 직렬화

			log.info("GMS 요청 바디: {}", jsonBody);

			HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

			ResponseEntity<String> response = restTemplate.exchange(
				gmsApiUrl,
				HttpMethod.POST,
				entity,
				String.class
			);

			log.info("GMS 응답 결과: {}", response.getBody());
			return response.getBody();

		} catch (HttpClientErrorException e) {
			log.error("GMS 요청 실패: {}", e.getResponseBodyAsString());
			return "GMS 오류: " + e.getResponseBodyAsString();
		} catch (Exception e) {
			log.error("GMS 요청 예외", e);
			return "GMS 예외: " + e.getMessage();
		}
	}
}