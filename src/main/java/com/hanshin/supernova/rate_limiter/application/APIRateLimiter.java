package com.hanshin.supernova.rate_limiter.application;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class APIRateLimiter {

    private final RedisClient redisClient;

    // LettuceBasedProxyManager 객체를 생성, 이 객체는 버킷의 생성 및 관리를 담당
    private LettuceBasedProxyManager<String> proxyManager;

    @PostConstruct
    public void init() {
        try {
            log.info("Initializing APIRateLimiter with RedisClient: {}", redisClient);
            StatefulRedisConnection<String, byte[]> connection = redisClient.connect(
                    RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
            this.proxyManager = LettuceBasedProxyManager.builderFor(connection)
                    .withExpirationStrategy(
                            io.github.bucket4j.distributed.ExpirationAfterWriteStrategy
                                    .basedOnTimeForRefillingBucketUpToMax(Duration.ofDays(1)))
                    .build();
            log.info("APIRateLimiter initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize APIRateLimiter", e);
            throw new IllegalStateException("Failed to initialize APIRateLimiter", e);
        }
    }

    /**
     * API 키에 해당하는 버킷에서 토큰을 소비하려고 시도하는 메서드
     */
    public long tryConsume(String key, int limit, int period) {
        Bucket bucket = proxyManager.builder().build(key, BucketConfiguration.builder()
                .addLimit(Bandwidth.simple(limit, Duration.ofSeconds(period)))
                .build());

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        long remainingTokens = probe.isConsumed() ? probe.getRemainingTokens() : -1;

        log.info("API Key: {}, Limit: {}, Period: {}, Consumed: {}, Remaining: {}, Time: {}",
                key, limit, period, probe.isConsumed(), remainingTokens, LocalDateTime.now());

        return remainingTokens;
    }

    /**
     * 토큰을 소비하지 않고 남은 토큰 수만 확인하는 메서드
     */
    public long getRemainingTokens(String key, int limit, int period) {
        Bucket bucket = proxyManager.builder().build(key, BucketConfiguration.builder()
                .addLimit(Bandwidth.simple(limit, Duration.ofSeconds(period)))
                .build());

        return bucket.getAvailableTokens();
    }
}