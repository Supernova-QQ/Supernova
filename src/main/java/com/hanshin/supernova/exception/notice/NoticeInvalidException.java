package com.hanshin.supernova.exception.notice;


import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class NoticeInvalidException extends BusinessException {
    public NoticeInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
