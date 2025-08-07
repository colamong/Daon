package com.daon.be.user.dto;

import com.daon.be.user.entity.Nation;

public record NationDto(
	String code,
	String nameKo,
	String nameEn
) {
	public static NationDto from(Nation nation) {
		return new NationDto(nation.getCode(), nation.getNameKo(), nation.getNameEn());
	}
}
