package com.daon.be.child.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChildExpressionRequestDto {

	private String sttText;

	public ChildExpressionRequestDto(String sttText) {
		this.sttText = sttText;
	}
}
