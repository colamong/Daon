package com.daon.be.ai.service;

import com.daon.be.ai.dto.ChildConversationAnalysisResultDto;

public interface ChildConversationGptService {

	ChildConversationAnalysisResultDto analyzeChildConversation(String childAnswerText);

}
