package com.hanshin.supernova.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
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
        V value =  jwtRedisTemplate.opsForValue().get(key);
        log.info("Redis에서 데이터 조회. 키: {}, 값: {}", key, value);
        return value;
    }

    public Boolean contains(String key) {
        return jwtRedisTemplate.hasKey(key);
    }

    public void delete(String key) {
        jwtRedisTemplate.delete(key);
    }

    /**
     * Redis에서 특정 키가 존재하는지 확인하는 메서드
     *
     * @param key Redis 키
     * @return 키가 존재하면 true, 그렇지 않으면 false
     */
    public boolean exists(String key) {
        Boolean hasKey = jwtRedisTemplate.hasKey(key);
        return hasKey != null && hasKey; // null 확인 후 Boolean 처리
    }
}