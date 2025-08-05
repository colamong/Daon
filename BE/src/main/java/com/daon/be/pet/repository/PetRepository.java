package com.daon.be.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daon.be.pet.entity.Pet;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
