package com.daon.be.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "User")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String nickname;

	@Column(name = "nation_code", nullable = false, length = 2)
	private String nationCode;

	@Enumerated(EnumType.STRING)
	private Provider provider = Provider.LOCAL;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	private LocalDateTime deletedAt;

	public enum Provider {
		LOCAL, GOOGLE
	}
}

