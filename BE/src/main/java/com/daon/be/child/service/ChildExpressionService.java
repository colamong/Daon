package com.daon.be.child.service;

import com.daon.be.child.dto.ChildExpressionResponseDto;

public interface ChildExpressionService {

	ChildExpressionResponseDto analyzeAndSave(Long childId, Long topicId, String sttText);
}
