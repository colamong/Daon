package com.daon.be.ai.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GptChildConversationResponseDto {

	// GPT 응답 요약
	private String emotion;
	private String summary;
	private LocalDateTime createdAt;
}
