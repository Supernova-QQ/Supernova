package com.hanshin.supernova.exception.s3;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class S3InvalidException extends BusinessException {

    public S3InvalidException(ErrorType errorType) {
        super(errorType);
    }
}
