package com.daon.be.child.dto;

import java.time.LocalDateTime;

import com.daon.be.conversation.entity.ConversationResult;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConversationResultResponseDto {

	private Long id;
	private Long topicId;
	private String emotionSummary;
	private String diarySummary;
	//private String interests;
	private LocalDateTime createdAt;

	public static ConversationResultResponseDto fromEntity(ConversationResult result) {
		return new ConversationResultResponseDto(
			result.getId(),
			result.getTopic().getId(),
			result.getEmotionReport(),
			result.getAnalysisResult(),
			result.getCreatedAt()
		);
	}

}
