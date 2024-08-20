package com.hanshin.supernova.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    CONFLICT_ERROR(HttpStatus.BAD_REQUEST, "예기치 못한 에러가 발생했습니다."),

    // common
    DUPLICATED_NAME_ERROR(HttpStatus.BAD_REQUEST, "중복된 이름입니다."),

    // community
    COMMUNITY_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "id에 해당하는 커뮤니티가 존재하지 않습니다."),
    NON_IDENTICAL_COMMUNITY_CREATOR_ERROR(HttpStatus.FORBIDDEN, "커뮤니티의 생성자가 아닙니다."),

    // admin 예외
    NON_ADMIN_AUTH_ERROR(HttpStatus.FORBIDDEN, "관리자 권한이 필요한 서비스 입니다."),

    // auth 예외
    NON_IDENTICAL_USER_ERROR(HttpStatus.FORBIDDEN, "작성자와 접근자가 일치하지 않습니다."),

    // 질문 예외
    NEITHER_BLANK_ERROR(HttpStatus.BAD_REQUEST, "제목과 내용은 빈 문자열일 수 없습니다."),
    QUESTION_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "id에 해당하는 질문이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String message;
}
