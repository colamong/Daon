package com.daon.be.conversation.repository;

import java.util.Optional;

import com.daon.be.conversation.entity.ConversationTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationTopicRepository extends JpaRepository<ConversationTopic, Long> {
		// 첫 토픽
		Optional<ConversationTopic> findFirstByOrderByIdAsc();

		// 최근 ID보다 큰 것 중 가장 작은 ID(= 다음 토픽)
		Optional<ConversationTopic> findFirstByIdGreaterThanOrderByIdAsc(Long id);

}
