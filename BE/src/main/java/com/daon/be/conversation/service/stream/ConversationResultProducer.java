package com.daon.be.conversation.service.streams;

import java.util.Map;

import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * 목적: 요청 스레드에서 "대화 결과 저장" 작업을 Redis Streams 큐에 적재(XADD)하고 즉시 반환.
 * - Stream 키: conversation:result
 * - 페이로드: childId, topicId, retry(0부터), ts(epoch ms)
 */
@Component
@RequiredArgsConstructor
public class ConversationResultProducer {

	public static final String STREAM_KEY = "conversation:result";
	private static final long MAX_STREAM_LENGTH = 100_000L; // 메모리 보호용 상한

	private final StringRedisTemplate redis; // 문자열 기반 -> XRANGE로 디버깅 쉬움

	/** 큐 적재. 성공 시 생성된 Stream RecordId 반환 */
	public RecordId enqueue(Long childId, Long topicId) {
		// 1) 메시지 페이로드 (운영 관측/디버깅 편의 위해 전부 String)
		Map<String, String> payload = Map.of(
			"childId", String.valueOf(childId),
			"topicId", String.valueOf(topicId),
			"retry", "0",
			"ts", String.valueOf(System.currentTimeMillis())
		);

		// 2) XADD: conversation:result 스트림에 메시지 추가
		var record = StreamRecords.newRecord()
			.in(STREAM_KEY)
			.ofMap(payload);

		RecordId id = redis.opsForStream().add(record);

		// 3) TRIM(approx): 스트림이 무한히 커지는 것 방지
		redis.opsForStream().trim(STREAM_KEY, MAX_STREAM_LENGTH, true);

		return id;
	}
}
