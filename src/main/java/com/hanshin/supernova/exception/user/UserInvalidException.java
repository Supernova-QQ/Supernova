package com.hanshin.supernova.exception.user;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class UserInvalidException extends BusinessException {

    public UserInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
