package com.daon.be.ai.service;

import org.springframework.stereotype.Service;

import com.daon.be.ai.client.OpenAiClient;
import com.daon.be.ai.dto.GptChildConversationResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChildConversationGptServiceImpl implements ChildConversationGptService {

	private final OpenAiClient openAiClient;

	@Override
	public GptChildConversationResponseDto analyzeText(String sttText) {
		String prompt = """
		다음 아이의 발화 내용을 바탕으로 감정 요약과 대화 전체 요약을 JSON 형태로 반환해.
		대화 전체 요약은 최대 60자, 감정 요약은 최대 100자로 한정되어있다.

		다음 형식으로:
		{
		  "emotion": "감정 요약",
		  "summary": "줄거리 요약"
		}

		입력 텍스트:
		%s
	""".formatted(sttText);

		String response = openAiClient.requestGpt(prompt);
		log.info("GMS 응답: {}", response);

		try {
			ObjectMapper objectMapper = new ObjectMapper();

			// GMS(OpenAI) 응답 파싱
			String content = objectMapper
				.readTree(response) // 전체 응답을 JsonNode로 파싱
				.path("choices")    // choices 배열
				.get(0)             // 첫 번째 choice
				.path("message")    // message 객체
				.path("content")    // content 문자열
				.asText();          // JSON 형식의 문자열

			log.info("GMS content: {}", content);

			// content JSON 문자열을 실제 DTO로 다시 파싱
			return objectMapper.readValue(content, GptChildConversationResponseDto.class);

		} catch (Exception e) {
			log.error("GMS 응답 파싱 실패", e);
			throw new RuntimeException("AI 응답 파싱 실패", e);
		}
	}

}
