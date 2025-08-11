package com.daon.be.conversation.controller;

import com.daon.be.child.repository.ChildProfileRepository;
import com.daon.be.conversation.dto.ChildAnswerRequestDto;
import com.daon.be.conversation.dto.ConversationTopicRequestDto;
import com.daon.be.conversation.dto.ConversationTopicResponseDto;
import com.daon.be.conversation.dto.GptAudioResponseDto;
import com.daon.be.conversation.entity.ConversationTopic;
import com.daon.be.conversation.repository.ConversationTopicRepository;
import com.daon.be.conversation.service.ChildAnswerService;

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

	@PostMapping("/answer")
	public ResponseEntity<GptAudioResponseDto> handleAnswer(@RequestBody ChildAnswerRequestDto dto) {
		GptAudioResponseDto response = childAnswerService.saveAnswerAndGetNextQuestionAudio(dto);
		return ResponseEntity.ok(response);
	}
	//관리자 새로운 대화주제 추가용
	@PostMapping("/topic")
	public ResponseEntity<ConversationTopicResponseDto> createTopic(
		@RequestBody ConversationTopicRequestDto dto) {
		ConversationTopic topic = childAnswerService.createTopic(dto);
		return ResponseEntity.ok(ConversationTopicResponseDto.fromEntity(topic));
	}


	@GetMapping("/start")
	public ResponseEntity<ConversationTopicResponseDto> startConversation(@RequestParam Long childId) {
			ConversationTopic nextTopicId = childAnswerService.getNextTopic(childId);
		return ResponseEntity.ok(ConversationTopicResponseDto.fromEntity(nextTopicId));
	}


}
