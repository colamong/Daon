package com.daon.be.conversation.service;

import com.daon.be.conversation.service.streams.ConversationFlushProperties;
import com.daon.be.conversation.service.streams.ConversationResultProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 종료 시 Flush 경로를 단일화하는 파사드.
 * - async 모드: Redis Streams로 큐 적재 (enqueue 실패 시 동기 flush로 폴백)
 * - sync  모드: 기존 동기 flush 경로
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlushFacade {

    private final ConversationFlushProperties props;
    private final SyncFlushService syncFlushService;
    private final ConversationResultProducer producer;

    public void flush(Long childId, Long topicId) {
        if (props.async()) {
            try {
                producer.enqueue(childId, topicId); // 비동기 저장: 큐 적재 후 즉시 반환
            } catch (Exception e) {
                // 안전장치: 큐 적재 실패 시 동기 경로로 폴백
                log.warn("[flush] enqueue failed, fallback to sync. childId={}, topicId={}, err={}",
                        childId, topicId, e.toString());
                try {
                    syncFlushService.flush(childId, topicId);
                } catch (Exception ex) {
                    log.error("[flush] sync fallback failed. childId={}, topicId={}, err={}",
                            childId, topicId, ex.toString());
                    throw ex; // 상위로 전파하여 모니터링/알람에 잡히게
                }
            }
        } else {
            syncFlushService.flush(childId, topicId); // 기존 동기 저장
        }
    }
}
