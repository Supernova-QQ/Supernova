package com.hanshin.supernova.exception.auth;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class UserAuthManagementInvalidException extends BusinessException {

    public UserAuthManagementInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
