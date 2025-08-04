package com.daon.be.calendar.entity;

import java.time.LocalDateTime;

import com.daon.be.conversation.entity.ConversationResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ImageDiary")
public class ImageDiary {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_id", nullable = false, unique = true)
	private Calendar calendar;

	private String imageUrl;

	@Column(columnDefinition = "TEXT")
	private String diaryText;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "conversation_result_id", nullable = false, unique = true)
	private ConversationResult conversationResult;

	// 그림일기 생성 팩토리 메소드
	public static ImageDiary create(ConversationResult result, String imageUrl, String diaryText) {
		ImageDiary diary = new ImageDiary();
		diary.setConversationResult(result);
		diary.setImageUrl(imageUrl);
		diary.setDiaryText(diaryText);
		diary.setCreatedAt(LocalDateTime.now());
		return diary;
	}
}