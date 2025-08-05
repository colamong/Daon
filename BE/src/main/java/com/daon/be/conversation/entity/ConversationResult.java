package com.daon.be.conversation.entity;

import java.time.LocalDateTime;

import com.daon.be.ai.dto.GptChildConversationResponseDto;
import com.daon.be.calendar.entity.Calendar;
import com.daon.be.calendar.entity.ImageDiary;
import com.daon.be.child.dto.ChildExpressionResponseDto;
import com.daon.be.child.entity.ChildProfile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ConversationResult", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"child_id", "topic_id"})
})
public class ConversationResult {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "child_id", nullable = false)
	private ChildProfile child;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "topic_id")
	private ConversationTopic topic;

	@Column(name = "stt_text", columnDefinition = "TEXT")
	private String sttText; // 전체 대화 STT 결과

	@Column(name = "analysis_result", columnDefinition = "TEXT")
	private String analysisResult; // 대화 요약 (그림일기용)

	@Column(name = "emotion_report", columnDefinition = "TEXT")
	private String emotionReport; // 감정 요약

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	@OneToOne(mappedBy = "conversationResult")
	private ImageDiary imageDiary;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id", nullable = false)
	private Calendar calendar;

	public static ConversationResult createFromAi(ChildProfile child, ConversationTopic topic,
		GptChildConversationResponseDto gptResponse, String sttText, Calendar calendar) {

		ConversationResult result = new ConversationResult();
		result.child = child;
		result.topic = topic;
		result.calendar = calendar;
		result.emotionReport = gptResponse.getEmotion();
		result.analysisResult = gptResponse.getSummary();
		result.sttText = sttText;
		result.createdAt = LocalDateTime.now();

		return result;
	}

	public void applyGptAnalysis(GptChildConversationResponseDto gptResponse) {
		this.analysisResult = gptResponse.getSummary();
		this.emotionReport = gptResponse.getEmotion();
	}

	public ChildExpressionResponseDto toResponseDto() {
		return new ChildExpressionResponseDto(this.emotionReport, this.analysisResult, this.createdAt);
	}

}

