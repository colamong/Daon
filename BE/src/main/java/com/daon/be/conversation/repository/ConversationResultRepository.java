package com.daon.be.conversation.repository;

import com.daon.be.conversation.entity.ConversationResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationResultRepository extends JpaRepository<ConversationResult, Long> {
	boolean existsByChildIdAndTopicId(Long childId, Long topicId);
}
