package com.daon.be.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Nation")
public class Nation {

	@Id
	@Column(length = 2)
	private String code;

	@Column(name = "name_ko", nullable = false)
	private String nameKo;

	@Column(name = "name_en", nullable = false)
	private String nameEn;
}