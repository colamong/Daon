package com.daon.be.ai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.daon.be.ai.client.OpenAiClient;
import com.daon.be.ai.dto.GptChildConversationResponseDto;
import com.daon.be.ai.dto.GptInterestAnalysisResponseDto;
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
	public GptChildConversationResponseDto analyzeEmotionAndSummary(String sttText) {
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
		log.info("GMS 감정/요약 분석 응답: {}", response);

		try {
			String content = extractGptContent(response);
			log.info("GMS content: {}", content);
			return objectMapper.readValue(content, GptChildConversationResponseDto.class);

		} catch (Exception e) {
			log.error("GMS 감정 요약/분석 응답 파싱 실패", e);
			throw new RuntimeException("AI 감정 요약/분석 응답 파싱 실패", e);
		}
	}

	@Override
	public GptInterestAnalysisResponseDto analyzeInterests(String sttText) {
		String prompt = """
		다음 아이의 발화 내용에서 아이의 '관심사'를 나타내는 핵심 키워드를 3개에서 5개 사이로 추출하고, 이를 바탕으로 아이에게 추천할 만한 활동이나 대화 주제를 포함한 간단한 리포트를 작성해줘.
		결과는 반드시 다음 JSON 형식으로 반환해야 해.

		{
		  "keywords": ["키워드1", "키워드2", "키워드3"],
		  "report": "아이는 [키워드]에 큰 관심을 보이고 있습니다. [키워드]와 관련된 책을 함께 읽거나, 관련 장소를 방문해보는 등 아이의 호기심을 자극하는 활동을 추천합니다.",
		  "summary": "줄거리 요약"
		}

		입력 텍스트:
		%s
		""".formatted(sttText);

		String response = openAiClient.requestGpt(prompt);
		log.info("GMS 관심사 분석 응답: {}", response);

		try {
			String content = extractGptContent(response);
			log.info("GMS interest content: {}", content);
			return objectMapper.readValue(content, GptInterestAnalysisResponseDto.class);
		} catch (Exception e) {
            log.error("GMS 관심사 요약/분석 응답 파싱 실패", e);
            throw new RuntimeException("AI 관심사 요약/분석 응답 파싱 실패", e);
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
