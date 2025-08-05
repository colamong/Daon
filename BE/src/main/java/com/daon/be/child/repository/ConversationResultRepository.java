package com.daon.be.child.repository;

import java.util.Optional;

import com.daon.be.conversation.entity.ConversationResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationResultRepository extends JpaRepository<ConversationResult, Long> {

	// 특정 아이가 특정 주제에 대해 대화 결과 이미 등록했는지 여부를 확인
	boolean existsByChildIdAndTopicId(Long childId, Long topicId);

	// 특정 아이의 특정 주제에 해당하는 대화 결과를 조회
	Optional<ConversationResult> findByChildIdAndTopicId(Long childId, Long topicId);
}
