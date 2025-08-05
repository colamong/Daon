package com.daon.be.child.service;

import com.daon.be.child.dto.ChildExpressionResponseDto;

public interface ChildExpressionService {

	public ChildExpressionResponseDto analyzeAndSave(Long childId, Long topicId);
}