package com.daon.be.conversation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daon.be.conversation.entity.ConversationPrompt;

public interface ConversationPromptRepository extends JpaRepository<ConversationPrompt, Long> {
	Optional<ConversationPrompt> findByTopic_IdAndStep(Long topicId, int step);
}
