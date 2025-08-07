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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversation")
public class ConversationController {

	private final ChildAnswerService childAnswerService;
	private final ChildProfileRepository childRepository;
	private final ConversationTopicRepository topicRepository;
	private final ConversationPromptService conversationPromptService;

	/**
	 * step1 질문 조회
	 */
	@GetMapping("/prompt")
	public ResponseEntity<String> getPromptForStep1(@RequestParam Long topicId) {
		ConversationPrompt prompt = conversationPromptService.getPromptByTopicAndStep(topicId, 1);
		return ResponseEntity.ok(prompt.getPrompt());
	}

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

	/**
	 * GPT를 통해 다음 질문 생성 (step2 이상부터)
	 */
	@PostMapping("/next-question")
	public ResponseEntity<String> getNextQuestion(@RequestBody ChildAnswerRequestDto dto) {
		if (dto.getStep() == 1) {
			// step1은 DB에 저장된 ConversationPrompt 사용
			ConversationPrompt prompt = conversationPromptService.getPromptByTopicAndStep(dto.getTopicId(), 1);
			return ResponseEntity.ok(prompt.getPrompt());
		} else {
			// step2 이상은 LLM 호출
			String nextPrompt = childAnswerService.generateNextPrompt(dto);
			return ResponseEntity.ok(nextPrompt);
		}
	}


	/**
	 * 디버깅용: Redis에서 문답 내용 조회
	 */
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
