package com.daon.be.child.service;

import java.time.LocalDate;

import com.daon.be.ai.dto.GptChildConversationResponseDto;
import com.daon.be.ai.service.ChildConversationGptService;
import com.daon.be.calendar.entity.Calendar;
import com.daon.be.calendar.repository.CalendarRepository;
import com.daon.be.child.dto.ChildExpressionResponseDto;
import com.daon.be.child.entity.ChildProfile;
import com.daon.be.child.repository.ChildProfileRepository;
import com.daon.be.child.repository.ConversationResultRepository;
import com.daon.be.conversation.entity.ConversationResult;
import com.daon.be.conversation.entity.ConversationTopic;
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
	private final CalendarRepository calendarRepository;
	private final ChildConversationGptService gptService;

	@Override
	@Transactional
	public ChildExpressionResponseDto analyzeAndSave(Long childId, Long conversationResultId) {

		// conversationResultId로 ConversationResult 조회
		ConversationResult result = resultRepo.findById(conversationResultId)
			.orElseThrow(() -> new IllegalArgumentException("해당 발화 결과가 존재하지 않습니다."));

		// childId 일치 여부 검증
		if (!result.getChild().getId().equals(childId)) {
			throw new IllegalStateException("해당 아동의 발화 결과가 아닙니다.");
		}

		// STT 텍스트 추출
		String sttText = result.getSttText();
		if (sttText == null || sttText.isBlank()) {
			throw new IllegalStateException("STT 결과가 비어 있습니다.");
		}

		// GPT 분석 수행
		GptChildConversationResponseDto gptResponse = gptService.analyzeText(sttText);

		// 분석 결과 적용
		result.applyGptAnalysis(gptResponse);
		return ChildExpressionResponseDto.fromEntity(result);
	}

}
