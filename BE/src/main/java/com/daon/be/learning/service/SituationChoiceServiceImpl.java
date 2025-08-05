package com.daon.be.learning.service;

import org.springframework.stereotype.Service;

import com.daon.be.learning.entity.SituationChoice;
import com.daon.be.learning.repository.SituationChoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SituationChoiceServiceImpl implements SituationChoiceService {

	private final SituationChoiceRepository situationChoiceRepository;

	// questionId 기준으로 정답 보기 텍스트 반환
	@Override
	public String getCorrectChoiceText(Long questionId) {
		SituationChoice correctChoice = situationChoiceRepository
			.findByQuestionIdAndIsCorrectTrue(questionId)
			.orElseThrow(() -> new IllegalArgumentException("정답 보기를 찾을 수 없습니다."));
		return correctChoice.getChoiceText();
	}
}
