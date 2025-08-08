package com.daon.be.community.repository;

import com.daon.be.community.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.community.id = :communityId ORDER BY cm.sentAt ASC")
    List<ChatMessage> findByCommunityIdOrderBySentAtAsc(@Param("communityId") Long communityId);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.community.id = :communityId ORDER BY cm.sentAt DESC")
    List<ChatMessage> findByCommunityIdOrderBySentAtDesc(@Param("communityId") Long communityId);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.user.id = :userId ORDER BY cm.sentAt DESC")
    List<ChatMessage> findByUserIdOrderBySentAtDesc(@Param("userId") Long userId);
}