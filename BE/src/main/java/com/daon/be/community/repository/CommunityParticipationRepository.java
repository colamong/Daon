package com.daon.be.community.repository;

import com.daon.be.community.entity.Community;
import com.daon.be.community.entity.CommunityParticipation;
import com.daon.be.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityParticipationRepository extends JpaRepository<CommunityParticipation, Long> {
    
    // Spring Data JPA 네이밍 컨벤션 사용 (쿼리 자동 생성)
    List<CommunityParticipation> findByUserIdAndLeftAtIsNull(Long userId);
    
    List<CommunityParticipation> findByCommunityIdAndLeftAtIsNull(Long communityId);
    
    Optional<CommunityParticipation> findByCommunityAndUserAndLeftAtIsNull(Community community, User user);
    
    boolean existsByCommunityAndUser(Community community, User user);
    
    Long countByCommunityIdAndLeftAtIsNull(Long communityId);
}