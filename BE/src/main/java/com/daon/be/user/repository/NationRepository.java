package com.daon.be.user.repository;

import com.daon.be.user.entity.Nation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NationRepository extends JpaRepository<Nation, String> {
	// PK가 String(code)이기 때문에 제네릭 타입은 <Nation, String>
}
