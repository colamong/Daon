package com.daon.be.child.repository;

import com.daon.be.child.entity.ChildInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildInterestRepository extends JpaRepository<ChildInterest, Long> {
    List<ChildInterest> findByChildProfileId(Long childProfileId);
    void deleteByChildProfileIdAndNameIn(Long childProfileId, List<String> names);
}
