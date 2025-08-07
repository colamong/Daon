package com.daon.be.global.config;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import jakarta.annotation.PostConstruct;

@Component
public class RedisBeanDebugger {

	private final ApplicationContext context;

	public RedisBeanDebugger(ApplicationContext context) {
		this.context = context;
	}

	@PostConstruct
	public void checkRedisBean() {
		try {
			RedisConnectionFactory factory = context.getBean(RedisConnectionFactory.class);
			System.out.println("✅ RedisConnectionFactory loaded: " + factory.getClass().getName());
		} catch (Exception e) {
			System.err.println("❌ RedisConnectionFactory NOT FOUND");
		}
	}
}
