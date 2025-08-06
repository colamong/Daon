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
	public UserPetStatusDto getPetStatus(Long childId) {
		ChildProfile child = childRepository.findById(childId)
			.orElseThrow(() -> new IllegalArgumentException("자녀 없음"));

		UserPet pet = userPetRepository.findByChild(child)
			.orElseGet(() -> {
				Pet defaultPet = petRepository.findAll().stream()
					.findFirst()
					.orElseThrow(() -> new IllegalStateException("기본 펭귄 없음"));

				UserPet newPet = new UserPet();
				newPet.setChild(child);
				newPet.setPet(defaultPet);
				newPet.setCurrentStage(1);
				newPet.setExperience(0);
				newPet.setName("펭구");
				newPet.setCreatedAt(LocalDateTime.now());
				newPet.setLastInteraction(LocalDateTime.now());

				return userPetRepository.save(newPet);
			});


		int count = conversationRewardRepository.countByChild(child);
		return UserPetStatusDto.from(pet, count);
	}

	@Override
	@Transactional
	public void rewardAfterConversation(Long childId) {
		ChildProfile child = childRepository.findById(childId)
			.orElseThrow(() -> new IllegalArgumentException("자녀 없음"));

		int count = conversationRewardRepository.countByChild(child);

		// 경험치 지급 로그
		ConversationReward reward = new ConversationReward();
		reward.setChild(child);
		reward.setConversationCount(count);
		reward.setExperienceGained(10);
		conversationRewardRepository.save(reward);

		UserPet pet = userPetRepository.findByChild(child)
			.orElseGet(() -> {
				Pet defaultPet = petRepository.findById(1L)
					.orElseThrow(() -> new IllegalStateException("기본 펭귄 없음"));

				UserPet newPet = new UserPet();
				newPet.setChild(child);
				newPet.setPet(defaultPet);
				newPet.setName("펭구");
				newPet.setCurrentStage(1);
				newPet.setExperience(0);
				newPet.setCreatedAt(LocalDateTime.now());
				newPet.setLastInteraction(LocalDateTime.now());

				return userPetRepository.save(newPet); // ✅ 반드시 return 해줘야 함
			});



		pet.setExperience(pet.getExperience() + 10);
		pet.setLastInteraction(LocalDateTime.now());

		// 성장 조건 수식 기반 적용
		int currentStage = pet.getCurrentStage();
		int maxStage = pet.getPet().getMaxStage();

		if (currentStage < maxStage) {
			int nextStage = currentStage + 1;
			int requiredCount = (nextStage == 6) ? 12 : nextStage * 2;

			if (count >= requiredCount) {
				pet.setCurrentStage(nextStage);
			}
		}

		userPetRepository.save(pet);
	}

}

