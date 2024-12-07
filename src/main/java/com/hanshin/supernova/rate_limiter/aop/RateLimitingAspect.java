package com.hanshin.supernova.rate_limiter.aop;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.rate_limiter.annotation.RateLimit;
import com.hanshin.supernova.rate_limiter.application.APIRateLimiter;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// RateLimit 애노테이션을 처리할 Aspect 클래스인 RateLimitingAspect
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitingAspect {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingAspect.class);
    private final APIRateLimiter apiRateLimiter;
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // APIRateLimiter 객체를 사용하여 API 키에 해당하는 버킷에서 토큰을 소비하려고 시도한다.
        // 토큰이 충분하면 요청이 성공적으로 처리되고, 그렇지 않으면 예외가 발생한다.
        String key = evaluateKey(joinPoint, rateLimit.key());
        long remainingTokens = apiRateLimiter.tryConsume(key, rateLimit.limit(),
                rateLimit.period());

        if (remainingTokens >= 0) {
            return joinPoint.proceed();
        } else {
            throw rateLimit.exceptionClass().getDeclaredConstructor(String.class)
                    .newInstance("Rate limit exceeded for key: " + key);
        }
    }

    private String evaluateKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Object[] args = joinPoint.getArgs();

            StandardEvaluationContext context = new StandardEvaluationContext();

            // 메서드 파라미터 이름 정보가 없는 경우를 대비한 처리
            String[] parameterNames = signature.getParameterNames();
            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }

            // 메서드 파라미터 타입 정보를 이용한 대체 처리
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                context.setVariable("p" + i, args[i]);
            }

            // AuthUser 와 같이 특별한 경우 처리
            // args[i]가 AuthUser 타입일 때, 이를 "user"라는 이름으로 특별히 매핑
            for (Object arg : args) {
                if (arg instanceof AuthUser) {
                    context.setVariable("user", arg);
                    break;
                }
            }

            Expression expression = parser.parseExpression(keyExpression);
            return expression.getValue(context, String.class);
        } catch (Exception e) {
            // 예외 발생 시 기본 키 반환 또는 예외 처리
            return "default-key-" + System.currentTimeMillis();
        }
    }
}