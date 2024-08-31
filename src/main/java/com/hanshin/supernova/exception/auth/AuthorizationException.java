package com.hanshin.supernova.exception.auth;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class AuthorizationException extends BusinessException {
    public AuthorizationException(ErrorType errorType) {
        super(errorType);
    }
}
