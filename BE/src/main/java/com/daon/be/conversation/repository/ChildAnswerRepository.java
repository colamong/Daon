package com.daon.be.conversation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.daon.be.conversation.entity.ChildAnswer;

@Repository
public interface ChildAnswerRepository extends JpaRepository<ChildAnswer, Long> {
	List<ChildAnswer> findAllByChildId(Long childId);
	List<ChildAnswer> findAllByTopicId(Long topicId);

	List<ChildAnswer> findAllByChildIdAndTopicIdOrderByStep(Long childId, Long topicId);

	@Query("SELECT ca.topic.id FROM ChildAnswer ca WHERE ca.child.id = :childId ORDER BY ca.createdAt DESC")
	Optional<Long> findLatestTopicIdByChildId(@Param("childId") Long childId);




}
