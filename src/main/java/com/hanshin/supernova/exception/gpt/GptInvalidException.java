package com.hanshin.supernova.exception.gpt;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class GptInvalidException extends BusinessException {

    public GptInvalidException(ErrorType errorType) {
        super(errorType);

    }
}
