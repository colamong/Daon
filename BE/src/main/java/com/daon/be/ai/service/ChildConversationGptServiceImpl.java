package com.daon.be.ai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.daon.be.ai.client.OpenAiClient;
import com.daon.be.ai.dto.GptFullAnalysisResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChildConversationGptServiceImpl implements ChildConversationGptService {

	private final OpenAiClient openAiClient;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public GptFullAnalysisResponseDto analyzeConversation(String sttText) {
		String prompt = """
		다음 아이의 발화 내용을 바탕으로 네 가지 항목을 분석하고, 반드시 아래의 JSON 형식으로 반환해줘.
		1. emotion: 아이의 감정을 100자 이내로 요약
		2. summary: 대화 전체 내용을 60자 이내로 요약
		3. keywords: 아이의 '관심사'를 나타내는 핵심 키워드를 3~5개 추출한 JSON 배열
		4. report: 추출된 키워드를 바탕으로 아이에게 추천할 만한 활동이나 대화 주제를 포함한 간단한 리포트
		
		{
		  "emotion": "감정 요약...",
		  "summary": "대화 요약...",
		  "keywords": ["키워드1", "키워드2", "키워드3"],
		  "report": "아이는 [키워드]에 큰 관심을 보이고 있습니다. [키워드]와 관련된 활동을 추천합니다."
		}
		
		입력 텍스트:
		{
		%s
		""".formatted(sttText);

		String response = openAiClient.requestGpt(prompt);
		log.info("GMS 통합 분석 응답: {}", response);

		try {
			String content = extractGptContent(response);
			log.info("GMS content: {}", content);
			return objectMapper.readValue(content, GptFullAnalysisResponseDto.class);

		} catch (Exception e) {
			log.error("GMS 통합 분석 응답 파싱 실패", e);
			throw new RuntimeException("AI 통합 분석 응답 파싱 실패", e);
		}
	}

	private String extractGptContent(String gptResponseJson) throws JsonProcessingException {
		return objectMapper
			.readTree(gptResponseJson)
			.path("choices")
			.get(0)
			.path("message")
			.path("content")
			.asText();
	}
}
