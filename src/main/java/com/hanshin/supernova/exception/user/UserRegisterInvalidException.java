package com.hanshin.supernova.exception.user;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class UserRegisterInvalidException extends BusinessException {
    public UserRegisterInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
