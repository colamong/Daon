package com.daon.be.child.controller;

import com.daon.be.child.dto.InterestAnalysisResponseDto;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daon.be.child.dto.ChildExpressionResponseDto;
import com.daon.be.child.repository.ConversationResultRepository;
import com.daon.be.child.service.ChildExpressionService;
import com.daon.be.child.service.ChildInterestService;
import com.daon.be.conversation.entity.ConversationResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/children") // 더 넓은 역할을 포괄
@RequiredArgsConstructor
public class ChildExpressionController {

	private final ChildExpressionService childExpressionService;
	private final ChildInterestService childInterestService;
	private final ConversationResultRepository resultRepository;

	// 아이 발화 분석 (이미 저장된 ConversationResult 기반)
	@PostMapping("/{childId}/expressions/{conversationResultId}")
	public ResponseEntity<ChildExpressionResponseDto> analyzeExpression(
		@PathVariable Long childId,
		@PathVariable Long conversationResultId
	) {
		ChildExpressionResponseDto response = childExpressionService.analyzeAndSave(childId, conversationResultId);
		return ResponseEntity.ok(response);
	}

    // 아이 관심사 분석 (이미 저장된 ConversationResult 기반)
    @PostMapping("/{childId}/interests/{conversationResultId}")
    public ResponseEntity<InterestAnalysisResponseDto> analyzeInterests(
            @PathVariable Long childId,
            @PathVariable Long conversationResultId
    ) {
        InterestAnalysisResponseDto response = childInterestService.analyzeInterests(childId, conversationResultId);
		//ai 추천 관심사 교체
		childInterestService.syncAiKeywords(childId, response.getKeywords());
		//삭제 안하고 누적만 원하면 아래 코드로...
		//childInterestService.upsertAiKeywords(childId, response.getKeywords());
		return ResponseEntity.ok(response);
    }


	//자동 분기 버전
	// Controller
	@PostMapping("/{childId}/analysis/{conversationResultId}")
	public ResponseEntity<Map<String, Object>> analyzeByResult(
		@PathVariable Long childId,
		@PathVariable Long conversationResultId
	) {
		String category = resultRepository.findTopicCategoryByIdAndChildId(conversationResultId, childId)
			.orElseThrow(() -> new IllegalArgumentException(
				"conversationResult not found: id=" + conversationResultId + ", childId=" + childId));

		if ("EMOTION".equalsIgnoreCase(category)) {
			ChildExpressionResponseDto r = childExpressionService.analyzeAndSave(childId, conversationResultId);
			return ResponseEntity.ok(Map.of("category", "EMOTION", "data", r));
		}
		if ("INTEREST".equalsIgnoreCase(category)) {
			InterestAnalysisResponseDto r = childInterestService.analyzeInterests(childId, conversationResultId);
			childInterestService.syncAiKeywords(childId, r.getKeywords());
			return ResponseEntity.ok(Map.of("category", "INTEREST", "data", r));
		}
		throw new IllegalArgumentException("unsupported category: " + category);
	}


}
