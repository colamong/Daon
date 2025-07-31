package com.daon.be.child.dto;

import java.time.LocalDateTime;

import com.daon.be.conversation.entity.ConversationResult;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChildExpressionResponseDto {

	private String emotion;  // 감정 요약
	private String summary;  // 대화 요약
	private LocalDateTime createdAt;

	public static ChildExpressionResponseDto fromEntity(ConversationResult entity) {
		return new ChildExpressionResponseDto(
			entity.getEmotionReport(),
			entity.getAnalysisResult(),
			entity.getCreatedAt()
		);
	}
}
