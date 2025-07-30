package com.daon.be.child.service;

import java.util.List;

import com.daon.be.child.dto.ConversationRequestDto;

public interface ChildExpressionService {
	void analyzeAndSave(Long childId, List<ConversationRequestDto> dtoList);
}
