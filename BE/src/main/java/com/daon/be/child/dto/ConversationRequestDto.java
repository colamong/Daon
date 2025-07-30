package com.daon.be.child.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConversationRequestDto {

	private Long topicId;
	private String title;
	private String category;
	private List<QnA> conversation;

	@Getter @Setter
	public static class QnA {
		private String q;
		private String a;
	}
}

