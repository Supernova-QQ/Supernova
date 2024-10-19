package com.hanshin.supernova.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService<V> {

    private final RedisTemplate<String, V> jwtRedisTemplate;

    @Autowired
    public RedisService(@Qualifier("jwtRedisTemplate")RedisTemplate<String, V> jwtRedisTemplate) {
        this.jwtRedisTemplate = jwtRedisTemplate;
    }

    public void set(String key, V value, long timeoutSeconds) {
        jwtRedisTemplate.opsForValue().set(key, value, timeoutSeconds, TimeUnit.SECONDS);
    }

    public V getValue(String key) {
        return jwtRedisTemplate.opsForValue().get(key);
    }

    public Boolean contains(String key) {
        return jwtRedisTemplate.hasKey(key);
    }

    public void delete(String key) {
        jwtRedisTemplate.delete(key);
    }
}