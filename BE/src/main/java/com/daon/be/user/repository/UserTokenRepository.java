package com.daon.be.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daon.be.user.entity.UserToken;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
}
