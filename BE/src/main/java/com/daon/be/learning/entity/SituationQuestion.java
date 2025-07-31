package com.daon.be.learning.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "SituationQuestion")
public class SituationQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chapter_id", nullable = false)
	private Chapter chapter;

	@Column(name = "question_text", columnDefinition = "TEXT")
	private String questionText;

	private String audioUrl;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();
}
