package com.daon.be.pet.entity;

import java.time.LocalDateTime;

import com.daon.be.child.entity.ChildProfile;

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
@Table(name = "ConversationReward")
public class ConversationReward {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "child_id", nullable = false)
	private ChildProfile child;

	@Column(name = "conversation_count")
	private int conversationCount;

	@Column(name = "experience_gained")
	private int experienceGained = 10;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();
}
