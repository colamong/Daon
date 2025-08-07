package com.daon.be.conversation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

import com.daon.be.ai.client.GmsOpenAiClient;
import com.daon.be.calendar.entity.Calendar;
import com.daon.be.child.entity.ChildProfile;
import com.daon.be.child.repository.ChildProfileRepository;
import com.daon.be.conversation.dto.ChildAnswerRedisDto;
import com.daon.be.conversation.dto.ChildAnswerRequestDto;
import com.daon.be.conversation.dto.ConversationTopicRequestDto;
import com.daon.be.conversation.dto.GptAudioResponseDto;
import com.daon.be.conversation.entity.ChildAnswer;
import com.daon.be.conversation.entity.ConversationResult;
import com.daon.be.conversation.entity.ConversationTopic;
import com.daon.be.conversation.repository.ChildAnswerRepository;
import com.daon.be.conversation.repository.ConversationPromptRepository;
import com.daon.be.child.repository.ConversationResultRepository;
import com.daon.be.conversation.repository.ConversationTopicRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.daon.be.calendar.repository.CalendarRepository;

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
	private final CalendarRepository calendarRepository;


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

	/*public void saveAnsweredStepsToRedis(
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

	}*/

	@Transactional
	public GptAudioResponseDto saveAnswerAndGetNextQuestionAudio(ChildAnswerRequestDto dto) {
		Long childId = dto.getChildId();
		Long topicId = dto.getTopicId();
		int step = dto.getStep();

		String redisKey = String.format("child:%d:topic:%d:answers", childId, topicId);

		// 1. 프롬프트에서 질문 가져오기
		String question = conversationPromptRepository
			.findByTopic_IdAndStep(topicId, step)
			.map(p -> p.getPrompt())
			.orElseThrow(() -> new IllegalArgumentException("해당 프롬프트 없음"));

		// 2. Redis에 저장
		ChildAnswerRedisDto redisDto = ChildAnswerRedisDto.of(step, question, dto.getAnswer());
		redisTemplate.opsForList().rightPush(redisKey, redisDto);

		// 3. 마지막 스텝이면 마무리 멘트
		if (step >= 5) {
			String endMessage = "오늘 이야기 들려줘서 고마워! 다음에 또 이야기 나눠보자!";

			//대화 저장
			flushAnswersFromRedis(childId, topicId);

			// 아직 미구현
			// String mp3Url = gmsOpenAiClient.tts(endMessage);

			return new GptAudioResponseDto(endMessage, null, true);
		}

		// 4. 다음 질문 생성
		dto.setStep(step + 1); // 다음 질문 step으로 수정
		String nextQuestion = generateNextPrompt(dto);

		// 아직 미구현
		// String mp3Url = gmsOpenAiClient.tts(nextQuestion);

		return new GptAudioResponseDto(nextQuestion, null, false);
	}



	public List<Object> getAnswersInRedis(String redisKey) {
		return redisTemplate.opsForList().range(redisKey, 0, -1);
	}

	@Transactional
	public Long flushAnswersFromRedis(Long childId, Long topicId) {
		String redisKey = String.format("child:%d:topic:%d:answers", childId, topicId);
		List<Object> rawList = redisTemplate.opsForList().range(redisKey, 0, -1);
		if (rawList == null || rawList.isEmpty()) return (long)-1;

		List<ChildAnswerRedisDto> list = rawList.stream()
			.map(o -> (ChildAnswerRedisDto) o)
			.toList();

		String sttText = list.stream()
			.map(ChildAnswerRedisDto::answer)
			.collect(Collectors.joining(" "));

		ChildProfile child = childRepository.findById(childId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀 ID: " + childId));
		ConversationTopic topic = conversationTopicRepository.getReferenceById(topicId);

		// 오늘 날짜의 캘린더 row가 있는지 확인
		LocalDate today = LocalDate.now();
		Calendar calendar = calendarRepository.findByChildIdAndDate(childId, today)
			.orElseGet(() -> {
				Calendar newCalendar = new Calendar();
				newCalendar.setChild(child);
				newCalendar.setDate(today);
				return calendarRepository.save(newCalendar);
			});


		// 중복 확인 후 ConversationResult 저장
		LocalDateTime startOfToday = LocalDate.now().atStartOfDay();     // 오늘 00:00:00
		LocalDateTime startOfTomorrow = startOfToday.plusDays(1);        // 내일 00:00:00

		boolean exists = conversationResultRepository
			.countTodayByChildId(childId, startOfToday, startOfTomorrow) > 0;
		if (!exists) {
			ConversationResult result = new ConversationResult();
			result.setChild(child);
			result.setTopic(topic);
			result.setSttText(sttText);
			result.setCalendar(calendar);
			conversationResultRepository.save(result);
			redisTemplate.delete(redisKey);
			return result.getId();
		}

		redisTemplate.delete(redisKey);
		return conversationResultRepository.findByChildIdAndTopicId(childId, topicId).get().getId();

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

		// 2. 시스템 프롬프트 구성 (Pengu 캐릭터 역할 + topic 목적)
		String topicObjective = conversationTopicRepository.findById(topicId)
			.map(ConversationTopic::getDescription)
			.orElse("(설명이 없습니다)");

		String systemPrompt = buildSystemPrompt(topicObjective, prevQuestions.size() + 1);

		// 3. 전체 메시지 구성 (Q&A + 마지막 요청)
		List<Map<String, String>> messages = buildMessages(systemPrompt, prevQuestions, prevAnswers);
		System.out.println("GPT Prompt Messages: " + messages);

		// 4. LLM 호출
		String nextQuestion = gmsOpenAiClient.ask(messages);

		// 5. 질문 Redis에 저장
		redisTemplate.opsForList().rightPush(questionKey, nextQuestion);

		return nextQuestion;
	}

	// 시스템 프롬프트 템플릿 (Pengu 역할 + 평가 목적 통합 + 현재 질문 번호)
	public String buildSystemPrompt(String topicObjective, int questionNumber) {
		return String.join("\n\n",
			"너는 Pengu라는 감정 코칭 AI 펭귄이야.",
			"아이와 감정에 대해 대화할 때, 다음 규칙을 꼭 따라야 해:",
			"- 아이는 5~7세야.",
			"- 말투는 따뜻하고 부드럽게.",
			"- 질문은 반드시 한 번에 하나만.",
			"- 절대 요약, 칭찬, 설명하지 말고 질문만 해.",
			"- 질문은 꼭 친구처럼, 부드럽고 편안하게 말해줘. 너무 똑똑하거나 어른스럽게 들리면 안 돼.",
			"- 총 5번 질문할 수 있어.",
			"이번 대화에서 도와줄 목표는: " + topicObjective,
			"지금은 " + questionNumber + "번째 질문이야.",
			"지금까지의 대화 흐름을 참고해서, 아이의 감정을 더 잘 표현할 수 있게 다음 질문을 하나만 자연스럽게 만들어줘."
		);
	}


	// Q&A 기록을 messages 포맷으로 변환 + 마지막 요청 추가
	public List<Map<String, String>> buildMessages(String systemPrompt, List<Object> prevQuestions, List<Object> prevAnswers) {
		List<Map<String, String>> messages = new ArrayList<>();

		// system 역할 프롬프트
		messages.add(Map.of("role", "system", "content", systemPrompt));

		// assistant(user 질문) → user(아이 대답)
		for (int i = 0; i < prevQuestions.size(); i++) {
			String questionStr = prevQuestions.get(i).toString();
			messages.add(Map.of("role", "assistant", "content", questionStr));

			if (i < prevAnswers.size()) {
				Object answerObj = prevAnswers.get(i);
				String answerStr;

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
				messages.add(Map.of("role", "user", "content", answerStr));
			}
		}

		// 다음 질문 유도 요청
		messages.add(Map.of("role", "user", "content", "→ 다음 질문을 자연스럽게 1개만 만들어줘."));
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
