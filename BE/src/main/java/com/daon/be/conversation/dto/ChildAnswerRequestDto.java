package com.daon.be.conversation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChildAnswerRequestDto {

	@NotNull
	private Long childId;

	@NotNull
	private Long topicId;

	@Min(1)
	private int step;

	// 선택값 (널 허용) 기본값 빈 문자열로 두면 NPE 방지에 좋음
	private String question = "";
	private String answer   = "";

	// JSON 본문에 base64 문자열을 넣으면 Jackson이 자동으로 디코드
	private byte[] audioBytes;

	// 프런트가 파일명을 알고 있으면 힌트용 (확장자 추정)
	private String audioFilename;

	// 언어 코드 - 기본 korean
	private String language = "ko";

	// JSON을 base64 문자열 필드로 보내고 싶은 경우용
	private String audioBase64;

	private String aggregatedText;
}
