package com.daon.be.child.service;

import java.util.StringJoiner;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.daon.be.ai.client.OpenAiClient;
import com.daon.be.child.dto.ConversationRequestDto;
import com.daon.be.child.dto.ConversationResultResponseDto;
import com.daon.be.conversation.entity.ConversationResult;
import com.daon.be.child.repository.ConversationResultRepository;
import com.daon.be.topic.entity.Topic;
import com.daon.be.topic.repository.TopicRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildExpressionServiceImpl implements ChildExpressionService {

	private final OpenAiClient openAiClient;
	private final ConversationResultRepository resultRepository;
	private final TopicRepository topicRepository;

	@Transactional
	@Override
	public ConversationResultResponseDto analyzeExpression(Long childId, ConversationRequestDto dto) {

		// 1. 프롬프트 생성
		String prompt = buildPrompt(dto);

		// 2. GPT 호출 (GMS)
		String gptResponse = openAiClient.requestGpt(prompt);

		// 3. GPT 응답 파싱
		String emotion = extractEmotionFrom(gptResponse);
		String summary = extractSummaryFrom(gptResponse);

		// 4. Topic 조회
		Topic topic = topicRepository.findById(dto.getTopicId())
			.orElseThrow(() -> new IllegalArgumentException("해당 주제를 찾을 수 없습니다."));

		// 5. ConversationResult 저장
		ConversationResult result = ConversationResult.builder()
			.childId(childId)
			.topic(topic)
			.emotionReport(emotion)
			.analysisResult(summary)
			.build();

		resultRepository.save(result);

		// 6. 응답 DTO 반환
		return ConversationResultResponseDto.fromEntity(result);
	}

	private String buildPrompt(ConversationRequestDto dto) {
		StringJoiner sj = new StringJoiner("\n\n");

		sj.add("다음은 아이와의 대화 내용입니다:");

		for (ConversationRequestDto.QnA qa : dto.getConversation()) {
			sj.add("Q: " + qa.getQ());
			sj.add("A: " + qa.getA());
		}

		sj.add("""
		이 대화를 분석하여 아래 형식으로 응답해주세요.

		{
			"emotion": "기쁨",
			"summary": "친구랑 놀아서 즐거웠다"
		}
		""");

		return sj.toString();
	}

	private String extractEmotionFrom(String response) {
		// TODO: JSON 파싱 로직으로 대체 필요
		return "기쁨";
	}

	private String extractSummaryFrom(String response) {
		return "친구랑 놀아서 즐거웠다";
	}
}