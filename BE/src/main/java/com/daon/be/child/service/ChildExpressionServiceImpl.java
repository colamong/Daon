package com.daon.be.child.service;

import com.daon.be.ai.dto.GptChildConversationResponseDto;
import com.daon.be.ai.service.ChildConversationGptService;
import com.daon.be.child.dto.ChildExpressionResponseDto;
import com.daon.be.child.entity.ChildProfile;
import com.daon.be.child.repository.ChildProfileRepository;
import com.daon.be.conversation.entity.ConversationResult;
import com.daon.be.conversation.entity.ConversationTopic;
import com.daon.be.child.repository.ConversationResultRepository;
import com.daon.be.conversation.repository.ConversationTopicRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChildExpressionServiceImpl implements ChildExpressionService {

	private final ChildProfileRepository childRepo;
	private final ConversationTopicRepository topicRepo;
	private final ConversationResultRepository resultRepo;
	private final ChildConversationGptService gptService;

	@Override
	@Transactional
	public ChildExpressionResponseDto analyzeAndSave(Long childId, Long topicId, String sttText) {

		// 아이 조회
		ChildProfile child = childRepo.findById(childId)
			.orElseThrow(() -> new IllegalArgumentException("해당 아이 정보를 찾을 수 없습니다."));

		// 주제 조회
		ConversationTopic topic = topicRepo.findById(topicId)
			.orElseThrow(() -> new IllegalArgumentException("해당 주제 정보를 찾을 수 없습니다."));

		// 감정/요약 분석
		GptChildConversationResponseDto gptResponse = gptService.analyzeText(sttText);
		ConversationResult result = ConversationResult.createFromAi(child, topic, gptResponse, sttText);
		resultRepo.save(result);

		// 응답 DTO 반환
		return new ChildExpressionResponseDto(
			gptResponse.getEmotion(),
			gptResponse.getSummary(),
			gptResponse.getCreatedAt()
		);
	}
}
