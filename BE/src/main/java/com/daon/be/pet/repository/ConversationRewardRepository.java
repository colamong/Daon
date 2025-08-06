package com.daon.be.pet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daon.be.pet.entity.ConversationReward;
import com.daon.be.child.entity.ChildProfile;

public interface ConversationRewardRepository extends JpaRepository<ConversationReward, Long> {

	// 대화 횟수 (보상 로그 개수)
	int countByChild(ChildProfile child);

	// (선택) 보상 이력 전체 조회
	List<ConversationReward> findAllByChild(ChildProfile child);
}
