package com.hanshin.supernova.exception.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ErrorDto implements Serializable {

    private final String message;
    private final String reason;

    public ErrorDto(ErrorType message) {
        log.info("[ErrorType] Name -> {}", message.name());
        this.message = message.name();
        this.reason = message.getMessage();
    }

}
