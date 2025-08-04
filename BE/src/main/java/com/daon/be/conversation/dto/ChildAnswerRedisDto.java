package com.daon.be.conversation.dto;

import java.io.Serializable;

public record ChildAnswerRedisDto(
	int step,
	String question,
	String answer
) implements Serializable {
	public static ChildAnswerRedisDto of(int step, String question, String answer) {
		return new ChildAnswerRedisDto(step, question, answer);
	}
}

