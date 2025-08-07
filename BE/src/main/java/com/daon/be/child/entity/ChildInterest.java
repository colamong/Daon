package com.daon.be.child.entity;

import jakarta.persistence.*;
import lombok.*;
import com.daon.be.child.entity.ChildProfile;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildInterest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 관심사 이름
	@Column(nullable = false)
	private String name;

	// 어떤 자녀의 관심사인지
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "child_profile_id", nullable = false)
	private ChildProfile childProfile;
}
