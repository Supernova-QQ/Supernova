package com.hanshin.supernova.exception.community;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class CommunityInvalidException extends BusinessException {

    public CommunityInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
