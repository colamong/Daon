package com.daon.be.conversation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChildAnswerRequestDto {

	private Long childId;          // FK 매핑용 (Controller에서 ChildProfile 조회용)
	private Long topicId;          // FK 매핑용 (Controller에서 ConversationTopic 조회용)
	private int step;              // 몇 번째 질문인지
	private String question;       // 질문 내용 (예: "오늘 기분이 어땠어요?")
	private String answer;         // 아이의 음성/텍스트 답변 결과
}

