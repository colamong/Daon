package com.daon.be.ai.service;

import java.util.List;

import com.daon.be.ai.dto.GptFullAnalysisResponseDto;

public interface ChildConversationGptService {

	GptFullAnalysisResponseDto analyzeConversation(String sttText);
}
