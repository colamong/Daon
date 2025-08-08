package com.daon.be.conversation.dto;


public record GptAudioResponseDto(
	String text,         // 질문 or 마무리 멘트
	String audioUrl,     // mp3 URL (TTS 결과, 현재 null)
	boolean finished,     // 대화 종료 여부
	Long conversationResultIds
) {}