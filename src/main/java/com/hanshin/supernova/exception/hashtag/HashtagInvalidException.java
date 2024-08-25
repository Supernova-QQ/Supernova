package com.hanshin.supernova.exception.hashtag;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class HashtagInvalidException extends BusinessException {

    public HashtagInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
