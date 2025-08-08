package com.daon.be.conversation.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	@Transactional
	public GptAudioResponseDto saveAnswerAndGetNextQuestionAudio(ChildAnswerRequestDto dto) {
		Long childId = dto.getChildId();
		Long topicId = dto.getTopicId();
		int step = dto.getStep();

		String redisKey = String.format("child:%d:topic:%d:answers", childId, topicId);

		// 1. 이전 질문 저장된 Redis에서 가져오기
		List<Object> questionList = redisTemplate.opsForList().range(redisKey, step - 1, step - 1);
		String question = (questionList != null && !questionList.isEmpty()) ? questionList.get(0).toString() : "질문 없음";

		// 2. Redis에 저장
		ChildAnswerRedisDto redisDto = ChildAnswerRedisDto.of(step, question, dto.getAnswer());
		redisTemplate.opsForList().rightPush(redisKey, redisDto);

		// 3. 마지막 스텝이면 마무리 멘트
		if (step >= 5) {
			String endMessage = "오늘 이야기 들려줘서 고마워! 다음에 또 이야기 나눠보자!";

			//대화 저장
			flushAnswersFromRedis(childId, topicId);


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

	private Calendar ensureTodayCalendar(Long childId) {
		ChildProfile child = childRepository.findById(childId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀 ID: " + childId));

		LocalDate today = LocalDate.now();
		return calendarRepository.findByChildIdAndDate(childId, today)
			.orElseGet(() -> calendarRepository.save(Calendar.of(child, today)));
	}

	void clearConversationRedis(long childId, long topicId) {
		String qKey = "child:" + childId + ":topic:" + topicId + ":questions";
		String aKey = "child:" + childId + ":topic:" + topicId + ":answers";

		log.info("[clearConversationRedis] 시작 - childId={}, topicId={}", childId, topicId);

		Boolean qDeleted = redisTemplate.delete(qKey);
		Boolean aDeleted = redisTemplate.delete(aKey);

		log.info("[clearConversationRedis] qKey={} 삭제결과={}, 존재여부(삭제 후)={}",
			qKey, qDeleted, redisTemplate.hasKey(qKey));
		log.info("[clearConversationRedis] aKey={} 삭제결과={}, 존재여부(삭제 후)={}",
			aKey, aDeleted, redisTemplate.hasKey(aKey));

		log.info("[clearConversationRedis] 종료");
	}

	@Transactional
	public Long flushAnswersFromRedis(Long childId, Long topicId) {
		log.info("[flushAnswersFromRedis] 시작 - childId={}, topicId={}", childId, topicId);

		String redisKey = String.format("child:%d:topic:%d:answers", childId, topicId);
		List<Object> rawList = redisTemplate.opsForList().range(redisKey, 0, -1);
		log.info("[flushAnswersFromRedis] Redis answers key={} size={}", redisKey, (rawList == null ? null : rawList.size()));

		if (rawList == null || rawList.isEmpty()) {
			log.warn("[flushAnswersFromRedis] Redis에 저장된 answers 없음 - childId={}, topicId={}", childId, topicId);
			return -1L;
		}

		List<ChildAnswerRedisDto> list = rawList.stream()
			.map(o -> (ChildAnswerRedisDto) o)
			.toList();
		log.info("[flushAnswersFromRedis] 변환된 Answer DTO 개수={}", list.size());

		String sttText = list.stream().map(ChildAnswerRedisDto::answer).collect(Collectors.joining(" "));
		log.debug("[flushAnswersFromRedis] STT 합친 텍스트={}", sttText);

		ChildProfile child = childRepository.findById(childId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀 ID: " + childId));
		log.info("[flushAnswersFromRedis] ChildProfile 로드 성공 - id={}", child.getId());

		ConversationTopic topic = conversationTopicRepository.getReferenceById(topicId);
		log.info("[flushAnswersFromRedis] ConversationTopic 로드 성공 - id={}", topic.getId());

		Calendar calendar = ensureTodayCalendar(childId);
		log.info("[flushAnswersFromRedis] Calendar 준비 완료 - id={}, date={}", calendar.getId(), calendar.getDate());

		boolean existsToday = conversationResultRepository.existsByChildIdAndCalendarId(childId, calendar.getId());
		log.info("[flushAnswersFromRedis] 오늘 데이터 존재 여부={}", existsToday);

		if (existsToday) {
			Long id = conversationResultRepository.findByChildIdAndCalendarId(childId, calendar.getId())
				.map(ConversationResult::getId).orElse(-2L);
			log.info("[flushAnswersFromRedis] 이미 오늘 데이터 존재 - id={}", id);
			redisTemplate.delete(redisKey);
			return id;
		}

		ConversationResult result = ConversationResult.create(child, topic, sttText, calendar);
		try {
			conversationResultRepository.save(result);
			conversationResultRepository.flush();
			log.info("[flushAnswersFromRedis] ConversationResult 저장 성공 - id={}", result.getId());
		} catch (DataIntegrityViolationException e) {
			log.error("[flushAnswersFromRedis] 저장 중 무결성 예외 발생 - childId={}, calendarId={}, message={}",
				childId, calendar.getId(), e.getMessage());
			return conversationResultRepository.findByChildIdAndCalendarId(childId, calendar.getId())
				.map(ConversationResult::getId).orElseThrow(() -> e);
		} finally {
			clearConversationRedis(childId, topicId);
			log.info("[flushAnswersFromRedis] Redis keys 삭제 완료 - childId={}, topicId={}", childId, topicId);
		}

		log.info("[flushAnswersFromRedis] 종료 - 저장된 result id={}", result.getId());
		return result.getId();
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


	// 시스템 프롬프트 템플릿 (Pengu 역할 + 첫 질문 규칙 포함)
	public String buildSystemPrompt(String topicObjective, int questionNumber) {
			String base = String.join("\n\n",
				"너는 Pengu라는 감정 코칭 AI 펭귄이야.",
				"아이와 감정에 대해 대화할 때, 다음 규칙을 따라:",
				"- 대상: 5~7세.",
				"- 말투: 친구처럼 부드럽고 짧게.",
				"- 출력: 질문 1개만. 요약·칭찬·설명 금지.",
				"- 난이도: 아이가 바로 이해할 쉬운 말만 사용.",
				"- 금지: 색으로 감정 표현, 표정-감정 연결 같은 추상 요구 금지.",
				"- 방식: '감정 자체'를 캐내려 하지 말고, 오늘 있었던 구체적 행동·상황을 물어봐.",
				"- 반응: 아이가 '몰라요/대답 없음'이면 심화 질문 금지, 더 쉬운 일상 질문으로 전환.",
				"- 목표: 아이에게 부담을 주지 않고 자연스러운 대화를 이끌 것.",
				"- 총 질문 수: 5회."
			);

			String stepRule = (questionNumber == 1)
				? String.join("\n",
				"- 첫 질문 규칙:",
				"  1) 반드시 '감정과 관련된 경험'을 물어봐.",
				"  2) 기준은 '오늘/최근'으로 한정.",
				"  3) 예시 범주를 제시해 선택을 돕되 강요하지 마: 좋았던 일, 속상했던 일, 놀란 일, 무서웠던 일.",
				"  4) 한 번에 하나 또는 많아도 두 가지 감정 경험만 물어봐야 한다.",
				"  5) 긴 문장 금지. 한 문장, 15자 내외를 우선 고려."
			)
				: String.join("\n",
				"- 진행 질문 규칙:",
				"  1) 바로 직전 답변의 '행동/상황'에서 한 걸음만 확장.",
				"  2) 원인·분석·메타인지 요구 금지(왜 그랬는지, 먼저 움직인 곳 등).",
				"  3) 선택지·구체화 표현을 활용해 부담을 낮춰.",
				"  4) 필요하면 '그때 뭐 했어?', '누가 같이 있었어?'처럼 상황 묻기."
			);

			String goal = "이번 대화의 목표: " + topicObjective;
			String stepInfo = "지금은 " + questionNumber + "번째 질문이야. 질문은 1개만 출력해.";

			String ask = "위 규칙을 지켜, 5~7세가 바로 이해할 수 있는 매우 짧은 질문 1개만 만들어줘.";

			return String.join("\n\n", base, stepRule, goal, stepInfo, ask);
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
