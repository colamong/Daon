package com.daon.be.child.service;

import java.time.LocalDate;
import java.util.List;

import com.daon.be.ai.service.ChildConversationGptService;
import com.daon.be.child.dto.FullAnalysisResponseDto;
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

	private final ConversationResultRepository resultRepo;
	private final ChildConversationGptService gptService;

	@Override
	@Transactional
		return ChildExpressionResponseDto.fromEntity(result);
	}

	@Override
	@Transactional
	public InterestAnalysisResponseDto analyzeInterests(Long childId, Long conversationResultId) {
		// 1. conversationResultId로 ConversationResult 조회
		ConversationResult result = resultRepo.findById(conversationResultId)
			.orElseThrow(() -> new IllegalArgumentException("해당 발화 결과가 존재하지 않습니다."));

		// 2. childId 일치 여부 검증
		if (!result.getChild().getId().equals(childId)) {
			throw new IllegalStateException("해당 아동의 발화 결과가 아닙니다.");
		}

		// 3. STT 텍스트 추출
		String sttText = result.getSttText();
		if (sttText == null || sttText.isBlank()) {
			return new InterestAnalysisResponseDto(List.of(), "분석할 대화 내용이 없습니다.", result.getAnalysisResult());
		}

		// 4. GPT를 이용해 관심사 키워드 및 리포트 분석
		GptInterestAnalysisResponseDto gptResponse = gptService.analyzeInterests(sttText);

		// 5. 분석 결과(리포트)를 ConversationResult에 저장
		result.applyInterestAnalysis(gptResponse.getReport());

		// 6. 분석 결과(키워드, 리포트)를 DTO에 담아 컨트롤러에 반환
		return new InterestAnalysisResponseDto(gptResponse.getKeywords(), gptResponse.getReport(), result.getAnalysisResult());
	}

}
