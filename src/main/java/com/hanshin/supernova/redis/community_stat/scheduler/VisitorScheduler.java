package com.hanshin.supernova.redis.community_stat.scheduler;

import com.hanshin.supernova.redis.community_stat.domain.CommunityStats;
import com.hanshin.supernova.redis.community_stat.infrastructure.CommunityStatsRepository;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 일정 시간에 한 번씩 db 에 방문자 정보 저장.
 * 현재 1시간 단위로 설정되어 있다.
 * 로그인 정보를 기반으로 스케줄링 작업 진행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VisitorScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final CommunityStatsRepository communityStatsRepository;

//    @Scheduled(initialDelay = 3600000, fixedDelay = 3600000)
    @Scheduled(initialDelay = 1000, fixedDelay = 300000)
    public void updateVisitorData() {
        Set<String> keys = redisTemplate.keys("community:*:visit:*:*");

        for (String key : keys) {
            try {
                String[] parts = key.split(":");
                if (parts.length != 5) {    // 5부분으로 나눠지지 않았을 경우
                    log.warn("Invalid key format: {}", key);
                    continue;
                }

                // SingleVisitInterceptor 의 key 저장 구조를 참고하여 파싱 진행
                Long communityId = Long.parseLong(parts[1]);
                String visitorIdentifier = parts[3];
                String dateStr = parts[4];

                LocalDate date;
                try {
                    date = LocalDate.parse(dateStr);
                } catch (DateTimeParseException e) {
                    log.error("Failed to parse date '{}' from key: {}", dateStr, key, e);
                    continue;
                }

                ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
                String userAgent = valueOperations.get(key);

                if (!communityStatsRepository.existsByVisitorIdentifierAndDateAndCommunityId(
                        visitorIdentifier, date, communityId)
                ) {
                    CommunityStats visitor = CommunityStats.builder()
                            .userAgent(userAgent)
                            .visitorIdentifier(visitorIdentifier)
                            .date(date)
                            .communityId(communityId)
                            .build();

                    communityStatsRepository.save(visitor);
                    log.info("Saved new visitor: communityId={}, visitorIdentifier={}, date={}",
                            communityId, visitorIdentifier, date);
                }

                redisTemplate.delete(key);
                log.debug("Processed and deleted key: {}", key);
            } catch (Exception e) {
                log.error("Error processing key: {}", key, e);
            }
        }
    }
}