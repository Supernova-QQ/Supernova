package com.hanshin.supernova.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    CONFLICT_ERROR(HttpStatus.BAD_REQUEST, "예기치 못한 에러가 발생했습니다."),

    // 해시태그 예외
    HASHTAG_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "id 에 해당하는 해시태그가 존재하지 않습니다."),
    HASHTAG_MAX_SIZE_5_ERROR(HttpStatus.BAD_REQUEST, "해시태그는 최대 5개까지 등록 가능합니다.");

    private final HttpStatus status;
    private final String message;
}
