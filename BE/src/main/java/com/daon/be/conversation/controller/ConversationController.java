package com.daon.be.conversation.controller;

import com.daon.be.ai.client.GmsOpenAiSttClient;
import com.daon.be.conversation.dto.ChildAnswerRequestDto;
import com.daon.be.conversation.dto.ConversationTopicRequestDto;
import com.daon.be.conversation.dto.ConversationTopicResponseDto;
import com.daon.be.conversation.dto.GptAudioResponseDto;
import com.daon.be.conversation.entity.ConversationTopic;
import com.daon.be.conversation.repository.ConversationTopicRepository;
import com.daon.be.conversation.service.ChildAnswerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/conversation")
public class ConversationController {

	private final ChildAnswerService childAnswerService;
	private final ConversationTopicRepository topicRepository;
	private final GmsOpenAiSttClient sttClient; // ★ STT 전용 클라이언트 주입

	@PostMapping("/answer")
	public ResponseEntity<GptAudioResponseDto> handleAnswer(@RequestBody ChildAnswerRequestDto dto) {
		log.info("[answer:json] childId={}, topicId={}, step={}, answer_len={}",
			dto.getChildId(), dto.getTopicId(), dto.getStep(),
			dto.getAnswer() == null ? null : dto.getAnswer().length());
		return ResponseEntity.ok(childAnswerService.saveAnswerAndGetNextQuestionAudio(dto));
	}

	/**
	 * Multipart 통합 입력: 음성이면 STT로 텍스트 변환 후 기존 흐름 호출
	 * fields: childId, topicId, step, (optional) answer, (optional) language=ko
	 * file:   audio (mp3/wav/m4a/webm...)
	 */
	@PostMapping(value = "/answer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<GptAudioResponseDto> handleAnswerMultipart(
		@RequestParam Long childId,
		@RequestParam Long topicId,
		@RequestParam int step,
		@RequestParam(required = false) String answer,
		@RequestParam(defaultValue = "ko") String language,
		@RequestPart(name = "audio", required = false) MultipartFile audio
	) {
		// 1) 음성만 왔으면 STT 수행
		if ((answer == null || answer.isBlank()) && audio != null && !audio.isEmpty()) {
			try {
				String text = sttClient.transcribe(audio.getBytes(), audio.getOriginalFilename(), language);
				answer = (text == null ? "" : text.trim());
				log.info("[answer:multipart] STT ok len={}", answer.length());
			} catch (Exception e) {
				log.warn("[answer:multipart] STT failed: {}", e.getMessage());
				// 실패 시 answer는 빈 문자열로 진행(서비스에서 폴백 질문 처리)
				answer = (answer == null ? "" : answer);
			}
		}

		// 2) 서비스 DTO로 변환
		ChildAnswerRequestDto dto = new ChildAnswerRequestDto();
		dto.setChildId(childId);
		dto.setTopicId(topicId);
		dto.setStep(step);
		dto.setAnswer(answer);

		return ResponseEntity.ok(childAnswerService.saveAnswerAndGetNextQuestionAudio(dto));
	}

	// 관리자: 주제 추가
	@PostMapping("/topic")
	public ResponseEntity<ConversationTopicResponseDto> createTopic(@RequestBody ConversationTopicRequestDto dto) {
		ConversationTopic topic = childAnswerService.createTopic(dto);
		return ResponseEntity.ok(ConversationTopicResponseDto.fromEntity(topic));
	}

	// 대화 시작: 다음 주제 조회
	@GetMapping("/start")
	public ResponseEntity<ConversationTopicResponseDto> startConversation(@RequestParam Long childId) {
		ConversationTopic next = childAnswerService.getNextTopic(childId);
		return ResponseEntity.ok(ConversationTopicResponseDto.fromEntity(next));
	}
}
