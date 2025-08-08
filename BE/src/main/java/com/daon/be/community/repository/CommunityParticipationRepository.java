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
    
    @Query("SELECT cp FROM CommunityParticipation cp WHERE cp.user.id = :userId AND cp.leftAt IS NULL")
    List<CommunityParticipation> findActiveParticipationsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT cp FROM CommunityParticipation cp WHERE cp.community.id = :communityId AND cp.leftAt IS NULL")
    List<CommunityParticipation> findActiveParticipationsByCommunityId(@Param("communityId") Long communityId);
    
    @Query("SELECT cp FROM CommunityParticipation cp WHERE cp.community = :community AND cp.user = :user AND cp.leftAt IS NULL")
    Optional<CommunityParticipation> findActiveParticipation(@Param("community") Community community, @Param("user") User user);
    
    @Query("SELECT COUNT(cp) FROM CommunityParticipation cp WHERE cp.community.id = :communityId AND cp.leftAt IS NULL")
    Long countActiveParticipantsByCommunityId(@Param("communityId") Long communityId);
}