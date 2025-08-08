package com.daon.be.pet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.daon.be.child.entity.ChildProfile;
import com.daon.be.pet.entity.UserPet;

public interface UserPetRepository extends JpaRepository<UserPet, Long> {
	public Optional<UserPet> findByChild(ChildProfile child);

	@Query("""
        select up from UserPet up
        join fetch up.pet
        where up.child.id = :childId
    """)
	Optional<UserPet> findByChildIdWithPet(@Param("childId") Long childId);
}
