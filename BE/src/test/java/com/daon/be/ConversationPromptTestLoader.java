package com.daon.be;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConversationPromptTestLoader {

	private final RedisTemplate<String, List<String>> redisTemplate;

	@PostConstruct
	public void preloadPrompts() {
		Long topicId = 1L;
		String redisKey = "conversation:prompt:" + topicId;

		List<String> prompts = List.of(
			"오늘 기분은 어때?",
			"왜 그런 기분이 들었어?",
			"그럴 땐 어떻게 해?",
			"그 기분은 어떤 색깔 같아?",
			"내일은 어떤 기분이고 싶어?"
		);

		redisTemplate.opsForValue().set(redisKey, prompts);
		System.out.println("[TEST] Redis 질문 5턴 캐시 완료: " + redisKey);
	}
}

