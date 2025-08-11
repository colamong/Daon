package com.daon.be.child.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "child_interest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildInterest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 키워드 저장 컬럼: name 사용
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "author", nullable = false, length = 10)
	private InterestAuthor author;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "child_profile_id", nullable = false)
	private ChildProfile childProfile;

	// 선택 컬럼: 필요 시만 사용(기존 스키마 호환)
	@Column(name = "interest_type", length = 100)
	private String interestType;

	/* factory methods */
	public static ChildInterest of(ChildProfile child, String name, InterestAuthor author) {
		return ChildInterest.builder()
			.childProfile(child)
			.name(name)
			.author(author)
			.build();
	}

	public static ChildInterest defaultOf(ChildProfile child, String name) {
		return of(child, name, InterestAuthor.PARENT);
	}

	public static ChildInterest ofId(Long id) {
		return ChildInterest.builder().id(id).build();
	}
}
