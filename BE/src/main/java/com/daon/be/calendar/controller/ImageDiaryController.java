package com.daon.be.calendar.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daon.be.calendar.dto.ImageDiaryResponseDto;
import com.daon.be.calendar.service.ImageDiaryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diaries")
public class ImageDiaryController {

	private final ImageDiaryService imageDiaryService;

	//  그림일기 생성
	@PostMapping("/{conversationResultId}")
	public ResponseEntity<Long> createDiary(@PathVariable Long conversationResultId) {
		return ResponseEntity.ok(imageDiaryService.createDiary(conversationResultId));
	}

	// 그림일기 단건 조회
	@GetMapping("/{diaryId}")
	public ResponseEntity<ImageDiaryResponseDto> getDiary(@PathVariable Long diaryId) {
		return ResponseEntity.ok(imageDiaryService.getDiary(diaryId));
	}

	// 월별 그림일기 조회
	@GetMapping("/monthly")
	public ResponseEntity<List<ImageDiaryResponseDto>> getMonthlyDiaries(
		@RequestParam Long childId,
		@RequestParam int year,
		@RequestParam int month) {
		return ResponseEntity.ok(imageDiaryService.getMonthlyDiaries(childId, year, month));
	}
}

