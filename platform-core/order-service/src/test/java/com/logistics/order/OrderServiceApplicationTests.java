package com.logistics.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "management.health.redis.enabled=false")
@ActiveProfiles("test")
class OrderServiceApplicationTests {

	@MockBean
	private RedisConnectionFactory redisConnectionFactory;

	@MockBean
	private ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

	@Test
	void contextLoads() {
	}

}
