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

	@Column(name = "profile_img")
	private String profileImg; // S3 key 또는 (과거) 공개 URL

	private LocalDateTime deletedAt;

	public enum Provider {
		LOCAL, GOOGLE
	}

	public static User of(String email, String encodedPassword, String nickname, String nationCode) {
		User user = new User();
		user.setEmail(email);
		user.setPassword(encodedPassword);
		user.setNickname(nickname);
		user.setNationCode(nationCode);
		return user;
	}

}

