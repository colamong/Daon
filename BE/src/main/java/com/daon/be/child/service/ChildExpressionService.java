package com.daon.be.child.service;

import java.util.List;

import com.daon.be.child.dto.ChildExpressionResponseDto;
import com.daon.be.child.dto.FullAnalysisResponseDto;
import com.daon.be.child.dto.InterestAnalysisResponseDto;

public interface ChildExpressionService {

	public ChildExpressionResponseDto analyzeAndSave(Long childId, Long conversationResultId);
}