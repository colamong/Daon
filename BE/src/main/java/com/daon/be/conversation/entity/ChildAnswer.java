package com.daon.be.conversation.entity;

import java.time.LocalDateTime;

import com.daon.be.child.entity.ChildProfile;
import com.daon.be.conversation.dto.ChildAnswerRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ChildAnswer", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"child_id", "topic_id", "step"})
})
public class ChildAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "child_id", nullable = false)
	private ChildProfile child;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "topic_id")
	private ConversationTopic topic;

	private int step;

	private String question;

	@Column(columnDefinition = "TEXT")
	private String answer;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	public static ChildAnswer fromDto(ChildAnswerRequestDto dto, ChildProfile child, ConversationTopic topic) {
		ChildAnswer entity = new ChildAnswer();
		entity.child = child;
		entity.topic = topic;
		entity.step = dto.getStep();
		entity.question = dto.getQuestion();
		entity.answer = dto.getAnswer();
		return entity;
	}
}