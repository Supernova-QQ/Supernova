package com.hanshin.supernova.redis.visit;

import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class QuestionVisitInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(QuestionVisitInterceptor.class);
    private final RedisTemplate<String, String> redisTemplate;
    private final QuestionRepository questionRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {

        log.info("조회수 증가 시작!");

        String questionId = extractQuestionId(request);
        if (questionId == null) {
            return true;
        } else if (!questionRepository.existsById(Long.parseLong(questionId))) {
            throw new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR);
        }

        // IPv6 -> IPv4 변환
        String visitorIdentifier = request.getRemoteAddr();
        if (visitorIdentifier.equals("0:0:0:0:0:0:0:1")) {
            visitorIdentifier = "127.0.0.1";
        }

        String today = LocalDate.now().toString();

        String key = "question:" + questionId + ":visit:" + visitorIdentifier + ":" + today;

        String userAgent = request.getHeader("User-Agent");

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        // 새로운 접근자일 경우 Redis 에 방문 정보 저장
        if (!valueOperations.getOperations().hasKey(key)) {
            valueOperations.set(key, userAgent);
            log.info("질문 조회수 증가: questionId={}, visitorIdentifier={}", questionId,
                    visitorIdentifier);
        }

        return true;
    }

    private String extractQuestionId(HttpServletRequest request) {
        String path = request.getRequestURI();  // HTTP 요청의 URI 경로를 가져온다.
        Pattern pattern = Pattern.compile("/api/questions/(\\d+)"); // (\\d+): 하나 이상의 숫자를 캡처 그룹으로 지정한다.
        Matcher matcher = pattern.matcher(path);    // 주어진 path에 대해 패턴 매칭을 수행할 Matcher 객체를 생성한다.
        return matcher.find() ? matcher.group(1) : null;    // find() 는 패턴과 일치하는 부분을 찾고, 일치하는 부분이 있으면 group(1)로 첫 번째 캡처 그룹(숫자 부분)을 반환한다.
    }
}
