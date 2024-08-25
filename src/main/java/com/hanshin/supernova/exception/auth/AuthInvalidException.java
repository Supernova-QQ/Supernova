package com.hanshin.supernova.exception.auth;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class AuthInvalidException extends BusinessException {

    public AuthInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
