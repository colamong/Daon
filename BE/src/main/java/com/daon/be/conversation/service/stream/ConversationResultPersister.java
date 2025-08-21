package com.daon.be.conversation.service.streams;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.daon.be.conversation.service.ChildAnswerServiceImpl;

import lombok.RequiredArgsConstructor;

/**
 * 목적: Listener가 호출할 "멱등 저장" 엔트리포인트.
 * 구현: 기존 동기 flush (ChildAnswerServiceImpl.flushAnswersFromRedis)를 호출.
 */
@Service
@RequiredArgsConstructor
public class ConversationResultPersister {

	private final ChildAnswerServiceImpl childAnswerService; // 기존 flush 보유 서비스

	/**
	 * 멱등 저장: 재시도/중복 메시지가 와도 DB 최종 상태는 동일.
	 * @throws DataIntegrityViolationException 중복/제약 위반 시 상위에서 ACK 처리
	 */
	public void flushFromRedisToDbIdempotent(Long childId, Long topicId) throws DataIntegrityViolationException {
		// Listener에서는 aggregated/lastAnswer 폴백을 모르므로 null 전달
		childAnswerService.flushAnswersFromRedis(childId, topicId, null, null);
	}
}
