package com.daon.be.ai.service;

import org.springframework.stereotype.Service;

import com.daon.be.ai.client.OpenAiClient;
import com.daon.be.ai.dto.ChildConversationAnalysisResultDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChildConversationGptServiceImpl implements ChildConversationGptService {

	private final OpenAiClient openAiClient;

	@Override
	public ChildConversationAnalysisResultDto analyzeChildConversation(String childAnswerText) {
		// 1. 프롬프트 직접 구성
		String prompt = buildPrompt(childAnswerText);

		// 2. GPT API 호출
		String gptResponse = openAiClient.requestGpt(prompt);

		// 3. 응답 파싱 (실제 사용 시 JSON 파싱으로 교체 필요)
		return parseGptResponse(gptResponse);
	}

	// 프롬프트 구성 메서드
	private String buildPrompt(String userSpeech) {
		return String.format(
			"""
			다음은 아이가 말한 내용입니다:
			"%s"
			
			위 내용을 바탕으로 아래 항목을 각각 작성해 주세요.

			1. 감정을 한 문장으로 분석해 주세요. (emotionReport)
			2. 전체 대화를 1~2문장으로 요약해 주세요. (conversationSummary)
			3. 관심사를 보여주는 핵심 키워드 1개를 추출해 주세요. (interestKeyword)

			반드시 아래 JSON 형식으로만 응답해 주세요:
			{
			  "emotionReport": "...",
			  "conversationSummary": "...",
			  "interestKeyword": "..."
			}
			""", userSpeech
		);
	}

	// 간단한 파서 (실제로는 ObjectMapper 사용 권장)
	private ChildConversationAnalysisResultDto parseGptResponse(String gptResponse) {
		String emotion = extractValue(gptResponse, "emotionReport");
		String summary = extractValue(gptResponse, "conversationSummary");
		String keyword = extractValue(gptResponse, "interestKeyword");

		return ChildConversationAnalysisResultDto.builder()
			.emotionReport(emotion)
			.conversationSummary(summary)
			.interestKeyword(keyword)
			.build();
	}

	private String extractValue(String json, String key) {
		try {
			int start = json.indexOf("\"" + key + "\":\"") + key.length() + 4;
			int end = json.indexOf("\"", start);
			return json.substring(start, end);
		} catch (Exception e) {
			return "";
		}
	}
}
