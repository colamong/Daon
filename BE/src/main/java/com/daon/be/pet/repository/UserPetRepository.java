package com.daon.be.pet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daon.be.child.entity.ChildProfile;
import com.daon.be.pet.entity.UserPet;

public interface UserPetRepository extends JpaRepository<UserPet, Long> {
	public Optional<UserPet> findByChild(ChildProfile child);
}
