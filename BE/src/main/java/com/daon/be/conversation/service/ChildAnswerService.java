package com.daon.be.conversation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

import com.daon.be.ai.client.GmsOpenAiClient;
import com.daon.be.child.entity.ChildProfile;
import com.daon.be.child.repository.ChildProfileRepository;
import com.daon.be.conversation.dto.ChildAnswerRedisDto;
import com.daon.be.conversation.dto.ChildAnswerRequestDto;
import com.daon.be.conversation.dto.ConversationTopicRequestDto;
import com.daon.be.conversation.entity.ChildAnswer;
import com.daon.be.conversation.entity.ConversationResult;
import com.daon.be.conversation.entity.ConversationTopic;
import com.daon.be.conversation.repository.ChildAnswerRepository;
import com.daon.be.conversation.repository.ConversationPromptRepository;
import com.daon.be.child.repository.ConversationResultRepository;
import com.daon.be.conversation.repository.ConversationTopicRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChildAnswerService {

	private final ChildAnswerRepository childAnswerRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ChildProfileRepository childRepository;
	private final ConversationTopicRepository conversationTopicRepository;
	private final ConversationPromptRepository conversationPromptRepository;
	private final ConversationResultRepository conversationResultRepository;
	private final GmsOpenAiClient gmsOpenAiClient;

	@Value("${prompt.system.base}")
	private String basePrompt;


	//테스트 용도로 유지
	@Transactional
	public void saveAnswer(ChildAnswerRequestDto dto, ChildProfile child, ConversationTopic topic) {
		ChildAnswer answer = ChildAnswer.fromDto(dto, child, topic);
		childAnswerRepository.save(answer);
	}
	public ConversationTopic createTopic(ConversationTopicRequestDto dto) {
		ConversationTopic topic = new ConversationTopic();
		topic.setTitle(dto.getTitle());
		topic.setCategory(dto.getCategory());
		topic.setDescription(dto.getDescription());
		return conversationTopicRepository.save(topic);
	}

	public void saveAnsweredStepsToRedis(
		Long childId,
		Long topicId,
		Map<Integer, String> questions,
		Map<Integer, String> answers,
		List<Integer> answeredSteps
	) {
		String questionKey = String.format("child:%d:topic:%d:questions", childId, topicId);
		String answerKey = String.format("child:%d:topic:%d:answers", childId, topicId);

		for (Integer step : answeredSteps) {
			String q = questions.get(step);
			String a = answers.get(step);
			if (q != null && a != null) {
				redisTemplate.opsForList().rightPush(questionKey, q);
				ChildAnswerRedisDto dto = ChildAnswerRedisDto.of(step, q, a);
				redisTemplate.opsForList().rightPush(answerKey, dto);
			}
		}

	}

	public List<Object> getAnswersInRedis(String redisKey) {
		return redisTemplate.opsForList().range(redisKey, 0, -1);
	}

	@Async
	@Transactional
	public void flushAnswersFromRedis(Long childId, Long topicId) {
		String redisKey = String.format("child:%d:topic:%d:answers", childId, topicId);
		List<Object> rawList = redisTemplate.opsForList().range(redisKey, 0, -1);
		if (rawList == null || rawList.isEmpty()) return;

		List<ChildAnswerRedisDto> list = rawList.stream()
			.map(o -> (ChildAnswerRedisDto) o)
			.toList();

		ChildProfile child = childRepository.getReferenceById(childId);
		ConversationTopic topic = conversationTopicRepository.getReferenceById(topicId);

		// 중복 확인 후 ConversationResult 저장
		boolean exists = conversationResultRepository.existsByChildIdAndTopicId(childId, topicId);
		if (!exists) {
			String sttText = list.stream()
				.map(dto -> "q: " + dto.question() + " a: " + dto.answer())
				.collect(Collectors.joining(" "));

			ConversationResult result = new ConversationResult();
			result.setChild(child);
			result.setTopic(topic);
			result.setSttText(sttText);

			conversationResultRepository.save(result);
		}

		redisTemplate.delete(redisKey);
	}

	public String generateNextPrompt(ChildAnswerRequestDto dto) {
		Long childId = dto.getChildId();
		Long topicId = dto.getTopicId();
		int step = dto.getStep();

		// 1. 이전 문답 가져오기
		String questionKey = String.format("child:%d:topic:%d:questions", childId, topicId);
		String answerKey = String.format("child:%d:topic:%d:answers", childId, topicId);
		List<Object> prevQuestions = redisTemplate.opsForList().range(questionKey, 0, -1);
		List<Object> prevAnswers = redisTemplate.opsForList().range(answerKey, 0, -1);

		// 2. 시스템 프롬프트 구성
		String topicDescription = conversationTopicRepository.findById(topicId)
			.map(ConversationTopic::getDescription)
			.orElse("");

		String stepPrompt = conversationPromptRepository.findByTopic_IdAndStep(topicId, step)
			.orElseThrow(() -> new IllegalArgumentException("해당 topicId, step 프롬프트 없음"))
			.getPrompt();

		String systemPrompt = buildSystemPrompt(basePrompt, topicDescription, stepPrompt);

		// 3. 전체 메시지 구성
		List<Map<String, String>> messages = buildMessages(systemPrompt, prevQuestions, prevAnswers);
		System.out.println("GPT Prompt Messages: " + messages);

		// 4. LLM 호출
		String nextQuestion = gmsOpenAiClient.ask(messages);

		// 5. 질문 Redis에 저장
		redisTemplate.opsForList().rightPush(questionKey, nextQuestion);

		return nextQuestion;
	}


	//시스템 프롬프트 조립 함수
	public String buildSystemPrompt(String basePrompt, String topicDescription, String stepPrompt) {
		return String.join("\n\n", basePrompt, topicDescription, stepPrompt);
	}

	public List<Map<String, String>> buildMessages(String systemPrompt, List<Object> prevQuestions, List<Object> prevAnswers) {
		List<Map<String, String>> messages = new ArrayList<>();
		messages.add(Map.of("role", "system", "content", systemPrompt));

		for (int i = 0; i < prevQuestions.size(); i++) {
			String questionStr = prevQuestions.get(i).toString();
			messages.add(Map.of("role", "assistant", "content", questionStr)); // 변경됨

			if (i < prevAnswers.size()) {
				Object answerObj = prevAnswers.get(i);
				String answerStr = null;

				if (answerObj instanceof ChildAnswerRedisDto dto) {
					answerStr = dto.answer();
				} else {
					try {
						String json = answerObj.toString();
						ChildAnswerRedisDto parsed = new ObjectMapper().readValue(json, ChildAnswerRedisDto.class);
						answerStr = parsed.answer();
					} catch (Exception e) {
						answerStr = answerObj.toString();
					}
				}

				messages.add(Map.of("role", "user", "content", answerStr)); // 변경됨
			}
		}

		return messages;
	}







	public Long getNextTopicId(Long childId) {
		Optional<Long> recentTopicIdOpt = childAnswerRepository.findLatestTopicIdByChildId(childId);

		Long nextTopicId = recentTopicIdOpt
			.map(id -> (id % 3) + 1)
			.orElse(1L);

		return nextTopicId;
	}

	public ConversationTopic getNextTopic(Long childId) {
		Long nextTopicId = getNextTopicId(childId);
		return conversationTopicRepository.findById(nextTopicId)
			.orElseThrow(() -> new IllegalStateException("ConversationTopic ID " + nextTopicId + " 없음"));
	}
}
