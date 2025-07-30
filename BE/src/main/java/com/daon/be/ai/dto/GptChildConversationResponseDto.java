package com.daon.be.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GptChildConversationResponseDto {

	// GPT 응답 요약 텍스트 전체
	private String result;
}
