package com.daon.be.ai.client;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiImageClient {

	private final RestTemplate restTemplate;

	@Value("${gms.api.key}")
	private String openAiApiKey;

	@Value("${gms.image.api.url}")
	private String openAiImageApiUrl;

	public String generateImage(String prompt) {
		try {

			// 이미지 생성 요청 바디 구성
			Map<String, Object> requestBody = Map.of(
				"model", "dall-e-3",
				"prompt", prompt,
				"size", "1792x1024"
			);

			// 요청 헤더 설정
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + openAiApiKey);

			// 요청 바디를 JSON 문자열로 변환
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonBody = objectMapper.writeValueAsString(requestBody);

			log.info("OpenAI 이미지 요청 바디: {}", jsonBody);

			HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

			// 이미지 생성 API 호출
			ResponseEntity<String> response = restTemplate.exchange(
				openAiImageApiUrl,
				HttpMethod.POST,
				entity,
				String.class
			);

			// 응답 JSON을 파싱하여 이미지 URL 추출
			Map<String, Object> responseMap = objectMapper.readValue(response.getBody(), Map.class);
			List<Map<String, String>> data = (List<Map<String, String>>) responseMap.get("data");
			String imageUrl = data.get(0).get("url");

			log.info("이미지 생성 결과 URL: {}", imageUrl);
			return imageUrl;

		} catch (HttpClientErrorException e) {
			log.error("이미지 생성 요청 실패: {}", e.getResponseBodyAsString());
			throw new RuntimeException("이미지 생성 실패: " + e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("이미지 생성 예외 발생", e);
			throw new RuntimeException("이미지 생성 예외: " + e.getMessage());
		}
	}
}
