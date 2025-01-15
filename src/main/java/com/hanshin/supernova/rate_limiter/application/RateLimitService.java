package com.hanshin.supernova.rate_limiter.application;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.application.AbstractValidateService;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.rate_limiter.dto.RateLimitStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RateLimitService extends AbstractValidateService {

    private final APIRateLimiter apiRateLimiter;

    @Transactional(readOnly = true)
    public RateLimitStatusResponse getRateLimitStatus(AuthUser user) {

        long remainingGenerate = getRemainingGenerate(user);

        return new RateLimitStatusResponse(remainingGenerate);
    }

    @Transactional(readOnly = true)
    public RateLimitStatusResponse getQuestionRateLimitStatus(AuthUser user, Long questionId) {

        Question findQuestion = getQuestionOrThrowIfNotExist(questionId);

        if (!user.getId().equals(findQuestion.getQuestionerId())) {
            throw new UserInvalidException(ErrorType.NON_IDENTICAL_USER_ERROR);
        }

        long remainingGenerate = getRemainingGenerate(user);

        return new RateLimitStatusResponse(remainingGenerate);
    }


    private long getRemainingGenerate(AuthUser user) {
        return apiRateLimiter.getRemainingTokens(
                "createAIAnswerTest:user" + user.getId(),
                5,
                24 * 60 * 60
        );
    }

}
