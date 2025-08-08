package com.daon.be.conversation.controller;

import com.daon.be.child.repository.ChildProfileRepository;
import com.daon.be.conversation.dto.ChildAnswerRequestDto;
import com.daon.be.conversation.dto.ConversationTopicRequestDto;
import com.daon.be.conversation.dto.ConversationTopicResponseDto;
import com.daon.be.conversation.dto.GptAudioResponseDto;
import com.daon.be.conversation.entity.ConversationPrompt;
import com.daon.be.conversation.entity.ConversationTopic;
import com.daon.be.conversation.repository.ConversationTopicRepository;
import com.daon.be.conversation.service.ChildAnswerService;
import com.daon.be.conversation.service.ConversationPromptService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversation")
public class ConversationController {

	private final ChildAnswerService childAnswerService;
	private final ChildProfileRepository childRepository;
	private final ConversationTopicRepository topicRepository;
	private final ConversationPromptService conversationPromptService;


	/**
	 * 각 스텝의 질문/답변을 Redis에 저장
	 */
	// @PostMapping("/answer")
	// public ResponseEntity<Void> saveAnswerToRedis(@RequestBody ChildAnswerRequestDto dto) {
	// 	childAnswerService.saveAnsweredStepsToRedis(
	// 		dto.getChildId(),
	// 		dto.getTopicId(),
	// 		Map.of(dto.getStep(), dto.getQuestion()),
	// 		Map.of(dto.getStep(), dto.getAnswer()),
	// 		List.of(dto.getStep())
	// 	);
	// 	return ResponseEntity.ok().build();
	// }

	/**
	 * 대화 종료 시 Redis → DB로 flush
	 */
	@PostMapping("/flush")
	public ResponseEntity<Long> flushAnswersAsync(@RequestParam Long childId, @RequestParam Long topicId) {
			Long resultId = childAnswerService.flushAnswersFromRedis(childId, topicId);
			return ResponseEntity.ok(resultId);  // 바로 응답 반환
	}

	@PostMapping("/answer")
	public ResponseEntity<GptAudioResponseDto> handleAnswer(@RequestBody ChildAnswerRequestDto dto) {
		GptAudioResponseDto response = childAnswerService.saveAnswerAndGetNextQuestionAudio(dto);
		return ResponseEntity.ok(response);
	}
	//관리자 새로운 대화주제 추가용
	@PostMapping("/topic")
	public ResponseEntity<ConversationTopicResponseDto> createTopic(
		@RequestBody ConversationTopicRequestDto dto) {
		ConversationTopic topic = conversationPromptService.createTopic(dto);
		return ResponseEntity.ok(ConversationTopicResponseDto.fromEntity(topic));
	}


	@GetMapping("/start")
	public ResponseEntity<ConversationTopicResponseDto> startConversation(@RequestParam Long childId) {
			ConversationTopic nextTopicId = childAnswerService.getNextTopic(childId);
		return ResponseEntity.ok(ConversationTopicResponseDto.fromEntity(nextTopicId));
	}


}
