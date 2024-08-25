package com.hanshin.supernova.exception.answer;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class AnswerInvalidException extends BusinessException {

    public AnswerInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
