package com.daon.be.learning.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daon.be.learning.service.SituationChoiceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/study/choice")
@RequiredArgsConstructor
public class SituationChoiceController {

	private final SituationChoiceService situationChoiceService;

	// 정답 보기 텍스트 반환
	@GetMapping("/correct/{questionId}")
	public ResponseEntity<String> getCorrectChoiceText(@PathVariable Long questionId) {
		String correctText = situationChoiceService.getCorrectChoiceText(questionId);
		return ResponseEntity.ok(correctText);
	}
}