package com.daon.be.child.service;

import com.daon.be.ai.dto.GptInterestAnalysisResponseDto;
import com.daon.be.ai.service.ChildConversationGptService;
import com.daon.be.child.dto.InterestAnalysisResponseDto;
import com.daon.be.child.entity.ChildInterest;
import com.daon.be.child.entity.ChildProfile;
import com.daon.be.child.entity.InterestAuthor;
import com.daon.be.child.repository.ChildInterestRepository;
import com.daon.be.child.repository.ChildProfileRepository;
import com.daon.be.child.repository.ConversationResultRepository;
import com.daon.be.conversation.entity.ConversationResult;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChildInterestServiceImpl implements ChildInterestService {

	private final ChildProfileRepository childProfileRepository;
	private final ChildInterestRepository childInterestRepository;
	private final ConversationResultRepository resultRepo;
	private final ChildConversationGptService gptService;

	@Override
	@Transactional
	public InterestAnalysisResponseDto analyzeInterests(Long childId, Long conversationResultId) {
		// 1. conversationResultId로 ConversationResult 조회
		ConversationResult result = resultRepo.findById(conversationResultId)
			.orElseThrow(() -> new IllegalArgumentException("해당 발화 결과가 존재하지 않습니다."));

		// 2. childId 일치 여부 검증
		if (!result.getChild().getId().equals(childId)) {
			throw new IllegalStateException("해당 아동의 발화 결과가 아닙니다.");
		}

		// 3. STT 텍스트 추출
		String sttText = result.getSttText();
		if (sttText == null || sttText.isBlank()) {
			return new InterestAnalysisResponseDto(List.of(), "분석할 대화 내용이 없습니다.", result.getAnalysisResult());
		}

		// 4. GPT를 이용해 관심사 키워드 및 리포트 분석
		GptInterestAnalysisResponseDto gptResponse = gptService.analyzeInterests(sttText);

		// 5. 분석 결과(리포트)를 ConversationResult에 저장
		result.applyInterestAnalysis(gptResponse);

		// 6. 분석 결과(키워드, 리포트)를 DTO에 담아 컨트롤러에 반환
		return new InterestAnalysisResponseDto(gptResponse.getKeywords(), gptResponse.getReport(), result.getAnalysisResult());
	}

	@Override
	public void upsertAiKeywords(Long childId, List<String> keywords) {
		if (keywords == null || keywords.isEmpty()) return;

		// 1) 입력 키워드 정규화 + 중복 제거(대소문자/공백 무시, 순서 보존)
		LinkedHashMap<String, String> normalized = new LinkedHashMap<>();
		for (String raw : keywords) {
			String norm = normalize(raw);
			if (norm == null) continue;
			normalized.putIfAbsent(normKey(norm), norm);
		}
		if (normalized.isEmpty()) return;

		// 2) 기존 데이터 인덱싱(name 기준)
		ChildProfile child = childProfileRepository.getReferenceById(childId);
		List<ChildInterest> existing = childInterestRepository.findByChildProfileId(childId);
		Map<String, ChildInterest> index = existing.stream()
			.filter(ci -> ci.getName() != null)
			.collect(Collectors.toMap(ci -> normKey(ci.getName()), Function.identity(), (a, b) -> a));

		// 3) 변경분만 저장
		List<ChildInterest> toSave = new ArrayList<>();
		for (Map.Entry<String, String> e : normalized.entrySet()) {
			String key = e.getKey();
			String value = e.getValue();

			ChildInterest found = index.get(key);
			if (found != null) {
				boolean changed = false;
				if (!Objects.equals(found.getName(), value)) { // 표기 차이 보정
					found.setName(value);
					changed = true;
				}
				if (found.getAuthor() != InterestAuthor.AI) {
					found.setAuthor(InterestAuthor.AI);
					changed = true;
				}
				if (changed) toSave.add(found);
			} else {
				toSave.add(ChildInterest.of(child, value, InterestAuthor.AI));
			}
		}

		if (!toSave.isEmpty()) childInterestRepository.saveAll(toSave);
	}

	@Override
	public void syncAiKeywords(Long childId, List<String> keywords) {
		if (keywords == null) return;

		// 1) 업서트로 최신 반영
		upsertAiKeywords(childId, keywords);

		// 2) 유지 대상 집합(name 정규화 키)
		Set<String> keep = keywords.stream()
			.map(this::normalize)
			.filter(Objects::nonNull)
			.map(this::normKey)
			.collect(Collectors.toSet());

		// 3) AI 작성 항목 중 keep에 없는 name 삭제
		List<ChildInterest> existing = childInterestRepository.findByChildProfileId(childId);
		List<String> toDelete = existing.stream()
			.filter(ci -> ci.getAuthor() == InterestAuthor.AI)
			.map(ChildInterest::getName)
			.filter(Objects::nonNull)
			.filter(n -> !keep.contains(normKey(n)))
			.toList();

		if (!toDelete.isEmpty()) {
			childInterestRepository.deleteByChildProfileIdAndNameIn(childId, toDelete);
		}
	}

	// 정규화 유틸
	private String normalize(String s) {
		if (s == null) return null;
		String t = s.trim();
		if (t.isEmpty()) return null;
		return t.length() > 100 ? t.substring(0, 100) : t;
	}

	private String normKey(String s) {
		return s.trim().toLowerCase();
	}

}
