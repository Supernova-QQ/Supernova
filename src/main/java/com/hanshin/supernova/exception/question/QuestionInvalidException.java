package com.hanshin.supernova.exception.question;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class QuestionInvalidException extends BusinessException {

    public QuestionInvalidException(ErrorType errorType) {
        super(errorType);
    }
}
