package com.daon.be.conversation.dto;

import com.daon.be.conversation.entity.ConversationPrompt;

public record ConversationPromptDto(
	Long id,
	Long topicId,
	int step,
	String prompt
) {
	public static ConversationPromptDto fromEntity(ConversationPrompt entity) {
		return new ConversationPromptDto(
			entity.getId(),
			entity.getTopic().getId(),
			entity.getStep(),
			entity.getPrompt()
		);
	}
}
