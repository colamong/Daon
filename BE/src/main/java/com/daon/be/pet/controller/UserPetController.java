package com.daon.be.pet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daon.be.pet.dto.UserPetStatusDto;
import com.daon.be.pet.service.UserPetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pet")
public class UserPetController {

	private final UserPetService userPetService;

	/**
	 * 펫 상태 조회 (경험치, 현재 레벨, 이미지, 경험치 진행률)
	 */
	@GetMapping("/{childId}")
	public ResponseEntity<UserPetStatusDto> getPetStatus(@PathVariable Long childId) {
		UserPetStatusDto status = userPetService.getPetStatus(childId);
		return ResponseEntity.ok(status);
	}

	/**
	 * 대화 종료 후 펭구 보상 지급
	 * - 경험치 10 추가
	 * - 필요 시 성장 처리
	 */
	@PostMapping("/reward/{childId}")
	public ResponseEntity<UserPetStatusDto> rewardAfterConversation(@PathVariable Long childId) {
		UserPetStatusDto status = userPetService.rewardAfterConversation(childId);
		return ResponseEntity.ok(status);
	}
}
