package com.hanshin.supernova.redis.question_visit.scheduler;

import com.hanshin.supernova.common.application.AbstractValidateService;
import com.hanshin.supernova.community.domain.Community;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.domain.QuestionView;
import com.hanshin.supernova.question.infrastructure.QuestionViewRepository;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionVisitScheduler extends AbstractValidateService {

    private final RedisTemplate<String, String> redisTemplate;
    private final QuestionViewRepository questionViewRepository;

    @Transactional
    @Scheduled(initialDelay = 3000, fixedDelay = 3000)    // 3초 지연, 3초 주기
    public void updateQuestionVisitorData() {
        Set<String> keys = redisTemplate.keys("question:*:visit:*:*");

        // 처리할 키 목록 로깅
        if (keys != null && !keys.isEmpty()) {
            log.info("처리할 키 목록: {}", keys);

        }

        for (String key : keys) {
            try {
                String[] parts = key.split(":");
                if (parts.length != 5) {
                    log.warn("게시글 조회 Key 형식이 잘못되었습니다: {}", key);
                    continue;
                }

                long questionId = Long.parseLong(parts[1]);
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

                Question findQuestion = getQuestionOrThrowIfNotExist(questionId);
                Community findCommunity = getCommunityOrThrowIfNotExist(findQuestion.getCommId());

                if (!questionViewRepository.existsByVisitorIdentifierAndViewedAtAndQuestionId(
                        visitorIdentifier, date, questionId)
                ) {
                    questionViewRepository.save(
                            QuestionView.builder()
                                    .userAgent(userAgent)
                                    .viewedAt(LocalDate.now())
                                    .questionId(questionId)
                                    .visitorIdentifier(visitorIdentifier)
                                    .commId(findCommunity.getId())
                                    .build());
                    findQuestion.updateViewCnt();

                    log.info("게시글 조회: questionId={}, visitorIdentifier={}, date={}", questionId,
                            visitorIdentifier, date);
                }

                redisTemplate.delete(key);
                log.debug("Processed and deleted key: {}", key);

            } catch (Exception e) {
                log.error("Error processing key: {}", key, e);
            }
        }

    }
}
