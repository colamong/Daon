package com.daon.be.child.repository;

import com.daon.be.child.entity.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildProfileRepository extends JpaRepository<ChildProfile, Long> {
	// 기본적인 findById, getReferenceById 등 지원됨
}
