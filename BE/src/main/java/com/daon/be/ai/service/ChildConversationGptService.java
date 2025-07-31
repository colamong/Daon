package com.daon.be.ai.service;

import com.daon.be.ai.dto.GptChildConversationResponseDto;

public interface ChildConversationGptService {

	GptChildConversationResponseDto analyzeText(String sttText);

}
