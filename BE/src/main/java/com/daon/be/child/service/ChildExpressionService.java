package com.daon.be.child.service;

import java.util.List;

import com.daon.be.child.dto.FullAnalysisResponseDto;

public interface ChildExpressionService {

	FullAnalysisResponseDto performFullAnalysis(Long childId, Long conversationResultId);
}