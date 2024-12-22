package com.hanshin.supernova.redis.hashtag_stat.scheduler;

import com.hanshin.supernova.redis.hashtag_stat.domain.HashtagStats;
import com.hanshin.supernova.redis.hashtag_stat.infrastructure.HashtagStatsRepository;
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
 * 일정 시간에 한 번씩 db 에 해시태그 사용 정보 저장. 현재 1시간 단위로 설정되어 있다. 로그인 정보를 기반으로 스케줄링 작업 진행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaggingScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final HashtagStatsRepository hashtagStatsRepository;

    @Scheduled(initialDelay = 3000, fixedDelay = 3000)    // 3초 지연, 3초 주기
    public void updateTaggingData() {
        Set<String> keys = redisTemplate.keys("hashtag:*:tagging:*:*");

        for (String key : keys) {
            try {
                log.info("해시태그 key = {}", key);

                String[] parts = key.split(":");
                if (parts.length != 5) {
                    log.warn("Invalid key format: {}", key);
                    continue;
                }

                // HashtagService 의 key 저장 구조를 참고하여 파싱 진행
                Long hashtagId = Long.parseLong(parts[1]);
                String taggerIdentifier = parts[3];
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

                HashtagStats tagger = HashtagStats.builder()
                        .userAgent(userAgent)
                        .taggerIdentifier(taggerIdentifier)
                        .date(date)
                        .hashtagId(hashtagId)
                        .build();

                hashtagStatsRepository.save(tagger);
                log.info("Saved new tagger: hashtagId={}, taggerIdentifier={}, date={}", hashtagId,
                        taggerIdentifier, date);

                redisTemplate.delete(key);
                log.debug("Processed and deleted key: {}", key);
            } catch (Exception e) {
                log.error("Error processing key: {}", key, e);
            }
        }
    }
}
