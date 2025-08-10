package com.daon.be.ai.service;

import java.util.List;

import com.daon.be.ai.dto.GptChildConversationResponseDto;
import com.daon.be.ai.dto.GptInterestAnalysisResponseDto;

public interface ChildConversationGptService {

	GptChildConversationResponseDto analyzeEmotionAndSummary(String sttText);

	GptInterestAnalysisResponseDto analyzeInterests(String sttText);
}
