package com.daon.be.child.service;

import java.util.List;

import com.daon.be.child.dto.InterestAnalysisResponseDto;

public interface ChildInterestService {
	public InterestAnalysisResponseDto analyzeInterests(Long childId, Long conversationResultId);
	void upsertAiKeywords(Long childId, List<String> keywords);
	void syncAiKeywords(Long childId, List<String> keywords);
}
