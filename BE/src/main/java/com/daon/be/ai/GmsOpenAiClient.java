package com.daon.be.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GmsOpenAiClient {

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 직렬화용

	@Value("${gms.api.url}")
	private String apiUrl;

	@Value("${gms.api.key}")
	private String gmsKey;

	@Value("${gms.api.model}")
	private String model;

	public GmsOpenAiClient(RestTemplateBuilder builder) {
		this.restTemplate = builder
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.name())
			.build();
	}

	public String ask(String prompt) {
		log.info("[GMS 요청 시작] 모델: {}, 프롬프트: {}", model, prompt);

		String jsonBody = """
		{
		  "model": "gpt-4.1-nano",
		  "messages": [{"role": "user", "content": "한국어로 인사해줘"}],
		  "max_tokens": 1024,
		  "temperature": 0.7,
		  "stream": false
		}
	""";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("Accept", "application/json");
		headers.set("Authorization", "Bearer " + gmsKey);

		HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
				apiUrl,
				HttpMethod.POST,
				entity,
				String.class
			);

			log.info("[GMS 응답 수신]: {}", response.getBody());
			return response.getBody();

		} catch (HttpClientErrorException e) {
			log.error("GMS 요청 실패: {}", e.getResponseBodyAsString());
			return "GMS 오류: " + e.getResponseBodyAsString();
		} catch (Exception e) {
			log.error("GMS 요청 예외", e);
			return "GMS 예외: " + e.getMessage();
		}
	}

	public String ask(List<Map<String, String>> messages) {
		try {
			Map<String, Object> requestBody = new LinkedHashMap<>();
			requestBody.put("model", model);
			requestBody.put("messages", messages);
			requestBody.put("max_tokens", 512);
			requestBody.put("temperature", 0.7);
			requestBody.put("stream", false);

			ObjectMapper objectMapper = new ObjectMapper();
			String jsonBody = objectMapper.writeValueAsString(requestBody);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(List.of(MediaType.APPLICATION_JSON));
			headers.set("Authorization", "Bearer " + gmsKey);

			HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

			ResponseEntity<String> response = restTemplate.exchange(
				apiUrl,
				HttpMethod.POST,
				entity,
				String.class
			);

			log.info("[GMS 응답 수신]: {}", response.getBody());
			return extractContent(response.getBody());

		} catch (HttpClientErrorException e) {
			log.error("GMS 요청 실패: {}", e.getResponseBodyAsString());
			return "GMS 오류: " + e.getResponseBodyAsString();
		} catch (Exception e) {
			log.error("GMS 요청 예외", e);
			return "GMS 예외: " + e.getMessage();
		}
	}

	private String extractContent(String json) {
		try {
			JsonNode root = objectMapper.readTree(json);
			return root.path("choices").get(0).path("message").path("content").asText();
		} catch (Exception e) {
			log.error("응답 파싱 실패", e);
			return "(응답 파싱 실패)";
		}
	}


}
