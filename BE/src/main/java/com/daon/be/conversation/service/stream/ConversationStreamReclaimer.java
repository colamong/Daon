package com.daon.be.conversation.service.streams;

import java.time.Duration;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.AutoClaimedMessages;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConversationStreamReclaimer {

	private final StringRedisTemplate redis;
	private final ConversationResultListener listener;

	private static final String STREAM   = ConversationResultProducer.STREAM_KEY; // "conversation:result"
	private static final String GROUP    = "db-writer";
	private static final String CONSUMER = "reclaimer-1"; // 회수 전용 컨슈머
	private static final Duration IDLE   = Duration.ofSeconds(30); // 이 시간 이상 방치되면 회수
	private static final int BATCH       = 100;

	// 다중 인스턴스 동시 실행 방지용 분산락
	private static final String LOCK_KEY = "conversation:result:reclaimer:lock";

	@Scheduled(fixedDelay = 30_000L, initialDelay = 30_000L)
	public void reclaim() {
		// 1) 분산락: 다른 인스턴스가 이미 돌고 있으면 스킵(30초 후 자동 해제)
		Boolean got = redis.opsForValue().setIfAbsent(LOCK_KEY, "1", Duration.ofSeconds(25));
		if (!Boolean.TRUE.equals(got)) return;

		try {
			ReadOffset cursor = ReadOffset.from("0-0"); // XAUTOCLAIM 시작 지점
			while (true) {
				AutoClaimedMessages<String, Object, Object> claimed =
					redis.opsForStream().autoClaim(
						STREAM,
						org.springframework.data.redis.connection.stream.Consumer.from(GROUP, CONSUMER),
						IDLE,
						cursor,
						BATCH
					);

				List<MapRecord<String, Object, Object>> records = claimed.getStreamRecords();
				if (records == null || records.isEmpty()) break;

				// 2) 회수된 메시지를 기존 Listener 로직으로 처리(ACK/재시도 포함)
				for (MapRecord<String, Object, Object> rec : records) {
					try {
						listener.onMessage(rec);
					} catch (Exception e) {
						// onMessage 내부에서 재시도/ACK까지 처리하므로 여기서는 로깅만
						log.warn("[auto-claim] onMessage error: {}", e.toString());
					}
				}

				// 3) 다음 스캔 시작점 갱신
				cursor = ReadOffset.from(claimed.getNextIdAsString());

				// 배치가 꽉 차지 않았으면 남은 건 없음
				if (records.size() < BATCH) break;
			}
		} catch (Exception e) {
			log.warn("[auto-claim] failed: {}", e.toString());
		} finally {
			try { redis.delete(LOCK_KEY); } catch (Exception ignore) {}
		}
	}
}
