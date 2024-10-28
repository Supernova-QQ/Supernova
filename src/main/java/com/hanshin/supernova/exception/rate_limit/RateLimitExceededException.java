package com.hanshin.supernova.exception.rate_limit;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class RateLimitExceededException extends BusinessException {

    public RateLimitExceededException(ErrorType errorType) {
        super(errorType);
    }

    public RateLimitExceededException(String message) {
        super(ErrorType.RATE_LIMIT_EXCEEDED_ERROR, message);
    }

    public RateLimitExceededException(ErrorType errorType, String customMessage) {
        super(errorType, customMessage);
    }

}
