package com.daon.be.conversation.dto;

import com.daon.be.conversation.entity.ConversationTopic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConversationTopicResponseDto {
	private Long id;
	private String name;
	private String description;

	public static ConversationTopicResponseDto fromEntity(ConversationTopic topic) {
		return new ConversationTopicResponseDto(
			topic.getId(),
			topic.getTitle(),
			topic.getDescription()
		);
	}

}

