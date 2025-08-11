package com.daon.be.child.entity;

public enum InterestAuthor {
	PARENT, AI;

	public static InterestAuthor of(String v) {
		return v == null ? PARENT : InterestAuthor.valueOf(v.toUpperCase());
	}
}
