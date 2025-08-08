package com.daon.be.pet.service;

import java.time.LocalDateTime;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daon.be.child.entity.ChildProfile;
import com.daon.be.child.repository.ChildProfileRepository;
import com.daon.be.pet.entity.ConversationReward;
import com.daon.be.pet.entity.Pet;
import com.daon.be.pet.entity.UserPet;
import com.daon.be.pet.repository.ConversationRewardRepository;
import com.daon.be.pet.repository.PetRepository;
import com.daon.be.pet.repository.UserPetRepository;
import com.daon.be.pet.dto.UserPetStatusDto;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserPetServiceImpl implements UserPetService {

	private final UserPetRepository userPetRepository;
	private final ConversationRewardRepository conversationRewardRepository;
	private final ChildProfileRepository childRepository;
	private final PetRepository petRepository;

	@Override
	@Transactional(readOnly = true)
	public UserPetStatusDto getPetStatus(Long childId) {
		ChildProfile child = childRepository.findById(childId)
			.orElseThrow(() -> new IllegalArgumentException("자녀 없음"));

		UserPet userPet = userPetRepository.findByChildIdWithPet(childId)
			.orElseGet(() -> {
				Pet defaultPet = petRepository.findById(1L)
					.orElseThrow(() -> new IllegalStateException("기본 펭귄 없음"));
				return userPetRepository.save(UserPet.create(child, defaultPet, "펭구"));
			});

		int count = conversationRewardRepository.countByChild(child);
		return UserPetStatusDto.fromEntity(userPet, count);
	}

	// UserPetServiceImpl 일부만 발췌

	@Override
	@Transactional
	public UserPetStatusDto rewardAfterConversation(Long childId) {
		ChildProfile child = childRepository.findById(childId)
			.orElseThrow(() -> new IllegalArgumentException("자녀 없음"));

		int prevTotal = conversationRewardRepository.countByChild(child);
		int newTotal = prevTotal + 1;

		ConversationReward reward = new ConversationReward();
		reward.setChild(child);
		reward.setConversationCount(newTotal);
		reward.setExperienceGained(10);
		conversationRewardRepository.save(reward);

		UserPet userPet = userPetRepository.findByChildIdWithPet(childId)
			.orElseGet(() -> {
				Pet defaultPet = petRepository.findById(1L)
					.orElseThrow(() -> new IllegalStateException("기본 펭귄 없음"));
				return userPetRepository.save(UserPet.create(child, defaultPet, "펭구"));
			});

		userPet.setExperience(userPet.getExperience() + 10);
		userPet.setLastInteraction(LocalDateTime.now());

		// 누적 대화 수(newTotal) 기준으로 승급 판단
		applyStageByTotal(userPet, newTotal);

		userPetRepository.save(userPet);
		return UserPetStatusDto.fromEntity(userPet, newTotal);
	}

	/**
	 * 누적 대화 수 기준으로 스테이지 계산.
	 * stage S에 도달하기 위한 누적 최소 대화 수 = S*(S-1)
	 * 예) S=2 -> 2, S=3 -> 6, S=4 -> 12 ...
	 */
	private void applyStageByTotal(UserPet up, int totalConversations) {
		int max = 7; // 명시 규칙
		int current = up.getCurrentStage();
		// 누적이 다음 스테이지의 최소치 이상이면 올라감. 여러 단계도 한 번에 처리.
		while (current < max && totalConversations >= requiredTotalForStage(current + 1)) {
			current++;
		}
		up.setCurrentStage(current);
	}

	/** S 스테이지 진입 최소 누적 대화 수 */
	private int requiredTotalForStage(int stage) {
		if (stage <= 1) return 0;
		return stage * (stage - 1);
	}

}


