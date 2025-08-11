package com.daon.be.child.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.daon.be.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ChildProfile")
public class ChildProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String name;

	@Column(name = "birth_date", nullable = false)
	private LocalDate birthDate;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(name = "profile_img")
	private String profileImg;

	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	// 관심사 연관관계 추가 (1:N)
	@OneToMany(mappedBy = "childProfile", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChildInterest> interests = new ArrayList<>();

	public enum Gender {
		MALE, FEMALE
	}

	// 관심사 편의 메서드
	public void addInterest(ChildInterest interest) {
		this.interests.add(interest);
		interest.setChildProfile(this);
	}

	public void removeInterest(String interestName) {
		this.interests.removeIf(i -> i.getInterestType().equals(interestName));
	}
}
