package com.daon.be.learning.repository;

import com.daon.be.learning.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeRepository extends JpaRepository<Theme,Long> {
}
