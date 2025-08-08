package com.daon.be.pet.service;

import com.daon.be.pet.dto.UserPetStatusDto;

public interface UserPetService {
	UserPetStatusDto getPetStatus(Long childId);
	UserPetStatusDto rewardAfterConversation(Long childId);
}
