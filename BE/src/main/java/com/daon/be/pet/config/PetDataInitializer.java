package com.daon.be.pet.config;

import com.daon.be.pet.entity.Pet;
import com.daon.be.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PetDataInitializer implements CommandLineRunner {

	private final PetRepository petRepository;

	@Override
	public void run(String... args) {
		// id=1인 기본 펭귄이 없으면 자동 삽입
		boolean exists = petRepository.existsById(1L);
		if (!exists) {
			Pet pet = new Pet();
			pet.setMaxStage(6);
			pet.setImageBaseUrl("/static/penguin/pet");
			petRepository.save(pet);
		}
	}
}
