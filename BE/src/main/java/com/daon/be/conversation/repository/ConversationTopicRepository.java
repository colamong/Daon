package com.daon.be.conversation.repository;

import java.util.Optional;

import com.daon.be.conversation.entity.ConversationPrompt;
import com.daon.be.conversation.entity.ConversationTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationTopicRepository extends JpaRepository<ConversationTopic, Long> {
}
