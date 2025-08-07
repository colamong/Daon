package com.daon.be.child.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daon.be.child.dto.ChildExpressionRequestDto;
import com.daon.be.child.dto.ChildExpressionResponseDto;
import com.daon.be.child.service.ChildExpressionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/children") // 더 넓은 역할을 포괄
@RequiredArgsConstructor
public class ChildExpressionController {

	private final ChildExpressionService childExpressionService;

	// 아이 발화 분석 (이미 저장된 ConversationResult 기반)
	@PostMapping("/{childId}/expressions/{conversationResultId}")
	public ResponseEntity<ChildExpressionResponseDto> analyzeExpression(
		@PathVariable Long childId,
		@PathVariable Long conversationResultId
	) {
		ChildExpressionResponseDto response = childExpressionService.analyzeAndSave(childId, conversationResultId);
		return ResponseEntity.ok(response);
	}
}
