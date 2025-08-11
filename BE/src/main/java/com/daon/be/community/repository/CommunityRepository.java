package com.daon.be.community.repository;

import com.daon.be.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    
    // Spring Data JPA 네이밍 컨벤션 사용 (쿼리 자동 생성)
    List<Community> findByTitleContaining(String title);
    
    @Query("SELECT c FROM Community c ORDER BY c.currentParticipants DESC")
    List<Community> findAllOrderByCurrentParticipantsDesc();
}