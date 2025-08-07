package com.daon.be.conversation.service;

import com.daon.be.conversation.dto.ConversationTopicRequestDto;
import com.daon.be.conversation.entity.ConversationPrompt;
import com.daon.be.conversation.entity.ConversationTopic;
import com.daon.be.conversation.repository.ConversationPromptRepository;
import com.daon.be.conversation.repository.ConversationTopicRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationPromptService {

	private final ConversationPromptRepository promptRepository;
	private final ConversationTopicRepository conversationTopicRepository;


	public ConversationTopic createTopic(ConversationTopicRequestDto dto) {
		ConversationTopic topic = new ConversationTopic();
		topic.setTitle(dto.getTitle());
		topic.setCategory(dto.getCategory());
		topic.setDescription(dto.getDescription());
		return conversationTopicRepository.save(topic);
	}

}
