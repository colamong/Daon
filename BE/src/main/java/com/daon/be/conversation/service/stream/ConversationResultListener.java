package com.daon.be.conversation.service.streams;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

/**
 * 목적: Streams에서 메시지를 읽어 Persister에 저장 요청.
 *  - 성공: XACK로 확정
 *  - 실패: retry 증가 후 재적재, 한계 초과 시 DLQ로 이동
 */
@Component
@RequiredArgsConstructor
public class ConversationResultListener implements StreamListener<String, MapRecord<String, Object, Object>> {

	private static final String STREAM = ConversationResultProducer.STREAM_KEY; // "conversation:result"
	private static final String GROUP  = "db-writer";
	private static final String DLQ    = "conversation:result:dlq";

	private final StringRedisTemplate redis;
	private final ConversationResultPersister persister;

	@Override
	public void onMessage(MapRecord<String, Object, Object> msg) {
		Map<Object, Object> v = msg.getValue();

		Long childId = Long.valueOf(v.get("childId").toString());
		Long topicId = Long.valueOf(v.get("topicId").toString());
		int retry = Integer.parseInt(Optional.ofNullable(v.get("retry")).orElse("0").toString());

		// 1) 경합 방지(동일 child/topic 동시 처리 방어): 짧은 분산락
		String lockKey = "child:" + childId + ":topic:" + topicId + ":flushing";
		boolean locked = Boolean.TRUE.equals(
			redis.opsForValue().setIfAbsent(lockKey, "1", 30, TimeUnit.SECONDS)
		);

		try {
			// 2) 멱등 저장
			persister.flushFromRedisToDbIdempotent(childId, topicId);

			// 3) 처리 성공 → ACK (중복 재처리 방지)
			ack(msg.getId());

		} catch (DataIntegrityViolationException dup) {
			// 4) 중복/무결성 위반은 이미 처리된 것으로 간주 → ACK
			ack(msg.getId());

		} catch (Exception e) {
			// 5) 실패 → 재시도 or DLQ
			if (retry < 5) {
				requeue(v, retry + 1);
			} else {
				toDlq(v);
			}
			// 같은 메시지가 PEL에 갇히지 않게 원본은 항상 ACK
			ack(msg.getId());

		} finally {
			if (locked) redis.delete(lockKey);
		}
	}

	private void ack(RecordId id) {
		redis.opsForStream().acknowledge(STREAM, GROUP, id);
	}

	private void requeue(Map<Object, Object> v, int nextRetry) {
		var next = new HashMap<String, String>();
		v.forEach((k, val) -> next.put(k.toString(), val.toString()));
		next.put("retry", String.valueOf(nextRetry));
		redis.opsForStream().add(MapRecord.create(STREAM, next));
	}

	private void toDlq(Map<Object, Object> v) {
		var m = new HashMap<String, String>();
		v.forEach((k, val) -> m.put(k.toString(), val.toString()));
		redis.opsForStream().add(MapRecord.create(DLQ, m));
	}
}
