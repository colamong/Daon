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
	public ChildExpressionResponseDto analyzeAndSave(Long childId, Long topicId) {

		// 기존 ConversationResult 조회
		ConversationResult result = resultRepo.findByChildIdAndTopicId(childId, topicId)
			.orElseThrow(() -> new IllegalArgumentException("먼저 STT 결과가 저장되어야 합니다."));

		// sttText 가져오기
		String sttText = result.getSttText();
		if (sttText == null || sttText.isBlank()) {
			throw new IllegalStateException("저장된 STT 결과가 비어 있습니다.");
		}

		// 감정 분석 및 요약
		GptChildConversationResponseDto gptResponse = gptService.analyzeText(sttText);

		// 결과 업데이트
		result.applyGptAnalysis(gptResponse);

		// 응답 반환
		return ChildExpressionResponseDto.fromEntity(result);
	}

}
