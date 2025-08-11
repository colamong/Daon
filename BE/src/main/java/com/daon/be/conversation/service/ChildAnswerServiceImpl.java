package com.daon.be.conversation.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daon.be.ai.client.GmsOpenAiClient;
import com.daon.be.calendar.entity.Calendar;
import com.daon.be.calendar.repository.CalendarRepository;
import com.daon.be.child.entity.ChildProfile;
import com.daon.be.child.repository.ChildProfileRepository;
import com.daon.be.child.repository.ConversationResultRepository;
import com.daon.be.conversation.controller.ConversationTtsController;
import com.daon.be.conversation.dto.ChildAnswerRedisDto;
import com.daon.be.conversation.dto.ChildAnswerRequestDto;
import com.daon.be.conversation.dto.ConversationTopicRequestDto;
import com.daon.be.conversation.dto.GptAudioResponseDto;
import com.daon.be.conversation.entity.ChildAnswer;
import com.daon.be.conversation.entity.ConversationResult;
import com.daon.be.conversation.entity.ConversationTopic;
import com.daon.be.conversation.repository.ChildAnswerRepository;
import com.daon.be.conversation.repository.ConversationTopicRepository;
import com.daon.be.tts.domain.port.SttClientPort;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChildAnswerServiceImpl implements ChildAnswerService {

	private final ChildAnswerRepository childAnswerRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ChildProfileRepository childRepository;
	private final ConversationTopicRepository conversationTopicRepository;
	private final ConversationResultRepository conversationResultRepository;
	private final GmsOpenAiClient gmsOpenAiClient;
	private final CalendarRepository calendarRepository;
	private final SttClientPort sttClient;

	// ObjectMapper 재사용
	private static final ObjectMapper OM = new ObjectMapper()
		.findAndRegisterModules()
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	// 기본 TTS
	private static final String CHILD_VOICE = "shimmer";
	private static final double CHILD_SPEED = 1.08;

	// 폴백 질문
	private static final List<String> FIRST_QUESTIONS = List.of(
		"오늘 있었던 일 중에 제일 기억나는 건 뭐야?",
		"오늘 재밌었던 일이 있었어?",
		"오늘 누가랑 놀았어?",
		"오늘 학교에서 제일 좋았던 순간은 뭐였어?",
		"오늘 놀이터에서 뭐 했어?"
	);
	private static final List<String> FOLLOWUP_QUESTIONS = List.of(
		"그때 누구랑 같이 있었어?",
		"그 다음에 뭐 했어?",
		"그걸 하고 기분이 어땠어?",
		"그때 어디였어?",
		"그 일을 다시 한다면 뭐가 가장 좋을까?"
	);

	/* ===== 테스트/유지용 메서드 ===== */
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

	/* ===== 핵심: 답변 저장 + 다음 질문 TTS URL 반환 ===== */
	@Transactional
	public GptAudioResponseDto saveAnswerAndGetNextQuestionAudio(ChildAnswerRequestDto dto) {
		final Long childId = dto.getChildId();
		final Long topicId = dto.getTopicId();
		final int step     = dto.getStep();

		// 1) 입력 검증
		if (childId == null || topicId == null || step <= 0) {
			return safeResponse(fallbackQuestion(step));
		}

		// 2) STT: answer가 비고 오디오 있으면 변환
		try {
			boolean empty = (dto.getAnswer() == null || dto.getAnswer().isBlank());
			byte[] audio = resolveAudioBytes(dto);
			if (empty && audio != null && audio.length > 0) {
				String lang = (dto.getLanguage() == null || dto.getLanguage().isBlank()) ? "ko" : dto.getLanguage();
				String text = sttClient.transcribe(audio, dto.getAudioFilename(), lang);
				if (text != null && !text.isBlank()) dto.setAnswer(text.trim());
				log.info("[STT] transcribed answer_len={}", (dto.getAnswer() == null ? 0 : dto.getAnswer().length()));
			}
		} catch (Exception e) {
			log.warn("[STT] transcribe failed: {}", e.getMessage());
		}

		final String qKey = "child:%d:topic:%d:questions".formatted(childId, topicId);
		final String aKey = "child:%d:topic:%d:answers".formatted(childId, topicId);
		final String tKey = "child:%d:topic:%d:stt_text".formatted(childId, topicId);

		// 3) 직전 질문 조회 (questions에서)
		String prevQuestion = "질문 없음";
		try {
			List<Object> one = redisTemplate.opsForList().range(qKey, step - 1, step - 1);
			if (one != null && !one.isEmpty()) prevQuestion = normalizeRedisValue(one.get(0));
		} catch (Exception e) {
			log.warn("Redis read skipped (prev question): {}", e.getMessage());
		}

		// 4) answers 리스트에 JSON으로 push
		try {
			ChildAnswerRedisDto redisDto = ChildAnswerRedisDto.of(step, prevQuestion, dto.getAnswer());
			String json = OM.writeValueAsString(redisDto);
			redisTemplate.opsForList().rightPush(aKey, json);
		} catch (Exception e) {
			log.warn("Redis push(answer) skipped: {}", e.getMessage());
		}

		// 5) 누적 텍스트 키(tKey)에 아이 답변만 이어붙임
		try {
			String a = dto.getAnswer();
			if (a != null && !a.isBlank()) {
				Object prev = redisTemplate.opsForValue().get(tKey);
				String next = (prev == null ? "" : String.valueOf(prev).trim());
				next = next.isBlank() ? a.trim() : (next + " " + a.trim());
				redisTemplate.opsForValue().set(tKey, next);
			}
		} catch (Exception e) {
			log.warn("Redis append(stt_text) skipped: {}", e.getMessage());
		}

		// 6) 마지막 스텝이면 flush → DB 저장(하루 1회). Redis 죽었을 때 폴백 사용
		if (step >= 5) {
			Long resultId;
			try {
				// 프론트에서 모아 보낸 전체 발화(aggregatedText)와 마지막 answer를 함께 넘김
				String aggFromClient = dto.getAggregatedText();   // null/blank 가능
				String lastAnswer    = dto.getAnswer();           // null/blank 가능
				resultId = flushAnswersFromRedis(childId, topicId, aggFromClient, lastAnswer);
			} catch (Exception e) {
				log.warn("flushAnswersFromRedis failed: {}", e.getMessage());
				resultId = -1L;
			}
			return new GptAudioResponseDto("오늘 이야기 고마워! 다음에 또 하자!", null, true, resultId);
		}

		// 7) 다음 질문 생성 (LLM 실패 시 폴백)
		String nextQuestion;
		try {
			dto.setStep(step + 1);
			nextQuestion = generateNextPrompt(dto);
			if (nextQuestion == null || nextQuestion.isBlank()) nextQuestion = fallbackQuestion(step + 1);
		} catch (Exception e) {
			log.warn("generateNextPrompt failed: {}", e.getMessage());
			nextQuestion = fallbackQuestion(step + 1);
		}

		// 8) 다음 질문 Redis 기록
		try {
			redisTemplate.opsForList().rightPush(qKey, nextQuestion);
		} catch (Exception e) {
			log.warn("Redis push(question) skipped: {}", e.getMessage());
		}

		return safeResponse(nextQuestion);
	}

	/** STT 입력용 오디오 바이트 추출: byte[] 우선, 없으면 base64 디코드 */
	private byte[] resolveAudioBytes(ChildAnswerRequestDto dto) {
		try {
			if (dto.getAudioBytes() != null && dto.getAudioBytes().length > 0) {
				return dto.getAudioBytes();
			}
			if (dto.getAudioBase64() != null && !dto.getAudioBase64().isBlank()) {
				return java.util.Base64.getDecoder().decode(dto.getAudioBase64());
			}
		} catch (IllegalArgumentException e) {
			log.warn("[STT] base64 decode failed: {}", e.getMessage());
		}
		return null;
	}

	/* ===== flush: tKey → answers → (프런트 폴백) 순으로 복구 후 저장 ===== */

	// 기존 시그니처는 유지(호환), 내부에서 오버로드 호출
	@Transactional
	public Long flushAnswersFromRedis(Long childId, Long topicId) {
		return flushAnswersFromRedis(childId, topicId, null, null);
	}

	/**
	 * 저장 우선순위:
	 *  1) aggregatedFallback(프론트가 보낸 전체 누적 텍스트)
	 *  2) tKey("...:stt_text") 값
	 *  3) aKey 리스트(JSON)에서 answer만 join
	 *  4) lastAnswerFallback(그래도 없으면 마지막 답변만이라도)
	 */
	@Transactional
	public Long flushAnswersFromRedis(Long childId, Long topicId,
		String aggregatedFallback,
		String lastAnswerFallback) {

		final String aKey = "child:%d:topic:%d:answers".formatted(childId, topicId);
		final String tKey = "child:%d:topic:%d:stt_text".formatted(childId, topicId);

		// 1) 프론트 폴백이 최우선
		String sttText = (aggregatedFallback == null) ? "" : aggregatedFallback.trim();
		if (sttText.isBlank()) {
			// 2) tKey 시도
			try {
				Object agg = redisTemplate.opsForValue().get(tKey);
				if (agg != null) sttText = String.valueOf(agg).trim();
			} catch (Exception e) {
				log.warn("[flush] read tKey failed: {}", e.getMessage());
			}
		}

		// 3) answers 리스트 복구(JSON → DTO → answer join)
		if (sttText.isBlank()) {
			List<Object> rawList;
			try {
				rawList = redisTemplate.opsForList().range(aKey, 0, -1);
				log.info("[flush] aKey size={}", (rawList == null ? 0 : rawList.size()));
			} catch (Exception ex) {
				log.warn("[flush] read aKey failed: {}", ex.getMessage());
				rawList = List.of();
			}

			final List<ChildAnswerRedisDto> list = (rawList == null ? List.<Object>of() : rawList).stream()
				.map(this::parseAnswerJsonSafely)
				.filter(dto -> dto != null && dto.answer() != null && !dto.answer().isBlank())
				.toList();

			sttText = list.stream()
				.map(ChildAnswerRedisDto::answer)
				.collect(Collectors.joining(" "))
				.trim();
		}

		// 4) 그래도 비면 마지막 답변만이라도
		if (sttText.isBlank() && lastAnswerFallback != null && !lastAnswerFallback.isBlank()) {
			sttText = lastAnswerFallback.trim();
			log.info("[flush] fallback to lastAnswer, len={}", sttText.length());
		}

		// === 영속화(하루 1회 정책 유지) ===
		final ChildProfile child = childRepository.findById(childId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀 ID: " + childId));
		final ConversationTopic topic = conversationTopicRepository.getReferenceById(topicId);
		final Calendar calendar = ensureTodayCalendar(childId);

		try {
			Optional<ConversationResult> existingOpt =
				conversationResultRepository.findByChildIdAndCalendarId(childId, calendar.getId());

			if (existingOpt.isPresent()) {
				ConversationResult existing = existingOpt.get();
				String old = existing.getSttText();
				boolean shouldUpdate =
					(sttText != null && !sttText.isBlank()) &&
						(old == null || old.isBlank() || sttText.length() > old.length());

				if (shouldUpdate) {
					existing.setSttText(sttText);
					conversationResultRepository.saveAndFlush(existing);
					log.info("[flush] UPDATE OK id={} (old_len={}, new_len={})",
						existing.getId(), (old == null ? 0 : old.length()), sttText.length());
				} else {
					log.info("[flush] SKIP UPDATE id={} (old_len>={})",
						existing.getId(), (sttText == null ? 0 : sttText.length()));
				}
				return existing.getId();
			}

			ConversationResult result = ConversationResult.create(child, topic, sttText, calendar);
			conversationResultRepository.saveAndFlush(result);
			log.info("[flush] INSERT OK id={} (stt_len={})", result.getId(), (sttText == null ? 0 : sttText.length()));
			return result.getId();

		} catch (DataIntegrityViolationException e) {
			log.warn("[flush] DataIntegrityViolation: {}", e.getMessage());
			return conversationResultRepository.findByChildIdAndCalendarId(childId, calendar.getId())
				.map(ConversationResult::getId).orElseThrow(() -> e);

		} finally {
			clearConversationRedis(childId, topicId);
		}
	}

	// ===== 프롬프트/LLM 보조 =====

	private String buildSystemPromptWithAntiRepeat(String topicObjective, int questionNumber, List<String> recent3) {
		String antiRepeat = String.join("\n",
			"- 중복 회피 규칙:",
			"  1) 아래 '최근 질문'과 단어 중복률이 50% 이상이면 안 됨.",
			"  2) 동일 문장 패턴 반복 금지(예: '~있었어?' 반복).",
			"  3) 8~18자 사이의 한 문장, 존대 대신 편한 말투 유지.",
			"  4) 어휘 다양화: '오늘/기분/재밌다' 같은 흔한 단어는 2개 이하로 제한.",
			"최근 질문: " + (recent3 == null || recent3.isEmpty() ? "(없음)" : String.join(" | ", recent3))
		);
		return buildSystemPrompt(topicObjective, questionNumber) + "\n\n" + antiRepeat;
	}

	private List<String> parseCandidatesJson(String json) {
		try {
			JsonNode node = OM.readTree(json);
			JsonNode arr = node.has("candidates") ? node.get("candidates") : node;
			List<String> out = new ArrayList<>();
			if (arr != null && arr.isArray()) {
				for (JsonNode n : arr) {
					if (n.isTextual()) out.add(n.asText());
				}
			}
			return out;
		} catch (Exception e) {
			log.warn("parseCandidatesJson failed: {}", e.getMessage());
			return List.of();
		}
	}

	private String pickNovelCandidate(List<String> candidates, List<String> recent3) {
		if (candidates == null || candidates.isEmpty()) return null;
		String best = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		for (String c : candidates) {
			if (c == null || c.isBlank()) continue;
			double maxSim = (recent3 == null ? 0.0
				: recent3.stream().mapToDouble(prev -> jaccard(c, prev)).max().orElse(0.0));
			double novelty = 1.0 - maxSim;
			int len = c.trim().length();
			double lenBonus = (len >= 8 && len <= 18) ? 0.05 : 0.0;
			double score = novelty + lenBonus;
			if (score > bestScore) { bestScore = score; best = c.trim(); }
		}
		return best;
	}

	public String generateNextPrompt(ChildAnswerRequestDto dto) {
		final Long childId = dto.getChildId();
		final Long topicId = dto.getTopicId();

		final String qKey = "child:%d:topic:%d:questions".formatted(childId, topicId);
		final String aKey = "child:%d:topic:%d:answers".formatted(childId, topicId);

		final List<Object> prevQuestionsRaw = safeRange(qKey, 0, -1);
		final List<Object> prevAnswersRaw  = safeRange(aKey, 0, -1);

		final List<String> prevQuestions = prevQuestionsRaw.stream()
			.map(this::normalizeRedisValue)
			.filter(s -> !s.isBlank())
			.toList();
		final List<String> recent3 = prevQuestions.stream()
			.skip(Math.max(0, prevQuestions.size() - 3))
			.toList();

		final String topicObjective = conversationTopicRepository.findById(topicId)
			.map(ConversationTopic::getDescription)
			.orElse("(설명이 없습니다)");

		final String systemPrompt = buildSystemPromptWithAntiRepeat(topicObjective, prevQuestions.size() + 1, recent3);

		final List<Map<String, String>> messages = buildMessages(systemPrompt, prevQuestionsRaw, prevAnswersRaw);

		messages.add(Map.of(
			"role", "user",
			"content",
			"""
			아래 형식의 JSON으로 '5개 후보'만 생성해줘.
			{"candidates": ["문장1","문장2","문장3","문장4","문장5"]}
			"""
		));

		String nextQuestion;
		try {
			String json = gmsOpenAiClient.ask(messages);
			List<String> candidates = parseCandidatesJson(json);
			nextQuestion = pickNovelCandidate(candidates, recent3);
			if (nextQuestion == null || nextQuestion.isBlank()) {
				nextQuestion = fallbackQuestion(prevQuestions.size() + 1);
			}
		} catch (Exception e) {
			log.warn("ask(candidates) failed: {}", e.getMessage());
			nextQuestion = fallbackQuestion(prevQuestions.size() + 1);
		}

		try { redisTemplate.opsForList().rightPush(qKey, nextQuestion); }
		catch (Exception e) { log.warn("Redis push nextQuestion skipped: {}", e.getMessage()); }

		return nextQuestion;
	}

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
			"  2) 원인·분석·메타인지 요구 금지(왜 그랬는지 등).",
			"  3) 선택지·구체화 표현을 활용해 부담을 낮춰.",
			"  4) 필요하면 '그때 뭐 했어?', '누가 같이 있었어?'처럼 상황 묻기."
		);

		String goal = "이번 대화의 목표: " + topicObjective;
		String stepInfo = "지금은 " + questionNumber + "번째 질문이야. 질문은 1개만 출력해.";
		String ask = "위 규칙을 지켜, 5~7세가 바로 이해할 수 있는 매우 짧은 질문 1개만 만들어줘.";

		return String.join("\n\n", base, stepRule, goal, stepInfo, ask);
	}

	public List<Map<String, String>> buildMessages(String systemPrompt, List<Object> prevQuestions, List<Object> prevAnswers) {
		List<Map<String, String>> messages = new ArrayList<>();

		if (systemPrompt != null && !systemPrompt.isBlank()) {
			messages.add(Map.of("role", "system", "content", systemPrompt));
		}

		for (int i = 0; i < prevQuestions.size(); i++) {
			String questionStr = normalizeRedisValue(prevQuestions.get(i));
			messages.add(Map.of("role", "assistant", "content", questionStr));

			if (i < prevAnswers.size()) {
				ChildAnswerRedisDto dto = parseAnswerJsonSafely(prevAnswers.get(i));
				String answerStr = (dto != null && dto.answer() != null) ? dto.answer() : normalizeRedisValue(prevAnswers.get(i));
				messages.add(Map.of("role", "user", "content", answerStr));
			}
		}

		messages.add(Map.of("role", "user", "content", "→ 다음 질문을 자연스럽게 1개만 만들어줘."));
		return messages;
	}

	/* ===== Redis/캘린더 보조 ===== */
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
		String tKey = "child:" + childId + ":topic:" + topicId + ":stt_text";
		try { redisTemplate.delete(qKey); } catch (Exception e) { log.warn("Redis delete skipped (qKey): {}", e.getMessage()); }
		try { redisTemplate.delete(aKey); } catch (Exception e) { log.warn("Redis delete skipped (aKey): {}", e.getMessage()); }
		try { redisTemplate.delete(tKey); } catch (Exception e) { log.warn("Redis delete skipped (tKey): {}", e.getMessage()); }
	}

	private List<Object> safeRange(String key, int start, int end) {
		try {
			List<Object> list = redisTemplate.opsForList().range(key, start, end);
			return (list != null) ? list : List.of();
		} catch (Exception e) {
			log.warn("Redis range skipped (key={}): {}", key, e.getMessage());
			return List.of();
		}
	}

	private String normalizeRedisValue(Object o) {
		if (o == null) return "";
		if (o instanceof byte[] bytes) return new String(bytes, StandardCharsets.UTF_8);
		return String.valueOf(o);
	}

	private ChildAnswerRedisDto parseAnswerJsonSafely(Object raw) {
		try {
			String s = (raw instanceof byte[] b) ? new String(b, java.nio.charset.StandardCharsets.UTF_8)
				: String.valueOf(raw);
			if (s == null || s.isBlank()) return ChildAnswerRedisDto.of(0, "질문 없음", "");
			return OM.readValue(s, ChildAnswerRedisDto.class);
		} catch (Exception ex) {
			log.warn("Failed to parse ChildAnswerRedisDto. raw={}, ex={}", raw, ex.toString());
			return ChildAnswerRedisDto.of(0, "질문 없음", String.valueOf(raw));
		}
	}

	private GptAudioResponseDto safeResponse(String question) {
		String url = ConversationTtsController.toStreamUrl(question, CHILD_VOICE, "wav", CHILD_SPEED, false);
		return new GptAudioResponseDto(question, url, false, 0L);
	}

	private String fallbackQuestion(int step) {
		ThreadLocalRandom rnd = ThreadLocalRandom.current();
		if (step <= 1) return FIRST_QUESTIONS.get(rnd.nextInt(FIRST_QUESTIONS.size()));
		return FOLLOWUP_QUESTIONS.get(rnd.nextInt(FOLLOWUP_QUESTIONS.size()));
	}

	/* ===== 유틸 ===== */
	private static String safe(String s) { return (s == null ? "" : s); }
	private double jaccard(String a, String b) {
		var sa = toTokenSet(a);
		var sb = toTokenSet(b);
		if (sa.isEmpty() && sb.isEmpty()) return 0.0;
		var inter = new java.util.HashSet<>(sa); inter.retainAll(sb);
		var union = new java.util.HashSet<>(sa); union.addAll(sb);
		return (double) inter.size() / (double) union.size();
	}
	private java.util.Set<String> toTokenSet(String s) {
		if (s == null) return java.util.Set.of();
		String norm = s.replaceAll("[^가-힣a-zA-Z0-9\\s]", " ")
			.toLowerCase()
			.replaceAll("\\s+", " ").trim();
		if (norm.isBlank()) return java.util.Set.of();
		String[] toks = norm.split(" ");
		java.util.Set<String> set = new java.util.HashSet<>();
		for (String t : toks) if (t.length() >= 2) set.add(t);
		return set;
	}

	// 주제 순환
	public Long getNextTopicId(Long childId) {
		Optional<Long> recentTopicIdOpt = childAnswerRepository.findLatestTopicIdByChildId(childId);
		return recentTopicIdOpt.map(id -> (id % 3) + 1).orElse(1L);
	}

	public ConversationTopic getNextTopic(Long childId) {
		Long nextTopicId = getNextTopicId(childId);
		return conversationTopicRepository.findById(nextTopicId)
			.orElseThrow(() -> new IllegalStateException("ConversationTopic ID " + nextTopicId + " 없음"));
	}
}
