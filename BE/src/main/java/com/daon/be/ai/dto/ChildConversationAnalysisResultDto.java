package com.daon.be.ai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildConversationAnalysisResultDto {

	private String emotionReport;
	private String conversationSummary;
	private String interestKeyword;

	public static ChildConversationAnalysisResultDto of(String emotionReport, String conversationSummary, String interestKeyword) {
		return new ChildConversationAnalysisResultDto(emotionReport, conversationSummary, interestKeyword);
	}
}
