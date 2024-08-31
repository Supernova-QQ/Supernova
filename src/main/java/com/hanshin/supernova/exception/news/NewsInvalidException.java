package com.hanshin.supernova.exception.news;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class NewsInvalidException extends BusinessException {

    public NewsInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
