package com.daon.be.community.repository;

import com.daon.be.community.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    // Spring Data JPA 네이밍 컨벤션 사용 (쿼리 자동 생성)
    List<ChatMessage> findByCommunityIdOrderBySentAtAsc(Long communityId);
    
    List<ChatMessage> findByCommunityIdOrderBySentAtDesc(Long communityId);
    
    List<ChatMessage> findByUserIdOrderBySentAtDesc(Long userId);
}