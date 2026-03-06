package com.logistics.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    public TokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String token, long durationMs) {
        if (token == null) {
            throw new IllegalArgumentException("Token must not be null");
        }
        redisTemplate.opsForValue().set(token, "blacklisted", durationMs, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token must not be null");
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}
