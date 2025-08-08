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
	private Long id;  // 이게 PRIMARY KEY

	@Column(nullable = false)
	private String name;

	// child_profile_id 컬럼 사용 (이미 올바름)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "child_profile_id", nullable = false)
	private ChildProfile childProfile;

	// child_id는 사용하지 않거나 별도 용도로 사용
}
