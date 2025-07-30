package com.daon.be.ai.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient {

	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${gms.api.key}")
	private String gmsApiKey;

	private static final String GMS_API_URL = "https://gms.ssafy.io/gmsapi/api.openai.com/v1/responses";

	public String requestGpt(String prompt) {
		// 요청 바디 구성
		Map<String, Object> requestBody = Map.of(
			"model", "gpt-4.1-nano",
			"input", prompt
		);

		// 헤더 구성
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(gmsApiKey); // Authorization: Bearer <GMS_KEY>

		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);

		// POST 요청 보내기
		ResponseEntity<String> response = restTemplate.exchange(
			GMS_API_URL,
			HttpMethod.POST,
			httpEntity,
			String.class
		);

		log.info("GMS 응답 결과: {}", response.getBody());
		return response.getBody();
	}
}