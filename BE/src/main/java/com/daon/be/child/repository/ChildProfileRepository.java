package com.daon.be.child.repository;

import com.daon.be.child.entity.ChildProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildProfileRepository extends JpaRepository<ChildProfile, Long> {
    List<ChildProfile> findByUserId(Long userId);
}
