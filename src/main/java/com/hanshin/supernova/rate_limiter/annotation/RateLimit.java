package com.hanshin.supernova.rate_limiter.annotation;

import com.hanshin.supernova.exception.rate_limit.RateLimitExceededException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 각 메서드에 적용할 사용자 정의 애노테이션인 RateLimit
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {
    String key();   // API 키를 지정하는 'key'라는 속성
    int limit();
    int period();
    Class<? extends RateLimitExceededException> exceptionClass() default RateLimitExceededException.class;
}