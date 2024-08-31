package com.hanshin.supernova.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    CONFLICT_ERROR(HttpStatus.BAD_REQUEST, "예기치 못한 에러가 발생했습니다."),

    // register 예외
    EMAIL_DUPLICATE_ERROR(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),
    NICKNAME_DUPLICATE_ERROR(HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),

    // common
    DUPLICATED_NAME_ERROR(HttpStatus.BAD_REQUEST, "중복된 이름입니다."),

    // community
    COMMUNITY_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "id에 해당하는 커뮤니티가 존재하지 않습니다."),
    NON_IDENTICAL_COMMUNITY_CREATOR_ERROR(HttpStatus.FORBIDDEN, "커뮤니티의 생성자가 아닙니다."),

    // admin 예외
    NON_ADMIN_AUTH_ERROR(HttpStatus.FORBIDDEN, "관리자 권한이 필요한 서비스 입니다."),

    // auth 예외
    NON_IDENTICAL_USER_ERROR(HttpStatus.FORBIDDEN, "작성자와 접근자가 일치하지 않습니다."),

    // 토큰 오류
    AUTHORIZATION_ERROR(HttpStatus.UNAUTHORIZED, "인증, 인가 오류"),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "해당 토큰은 만료된 토큰입니다."),
    NULL_TOKEN(HttpStatus.UNAUTHORIZED, "access token 이 존재하지 않습니다."),
    TOKEN_BLACKLISTED(HttpStatus.BAD_REQUEST, "해당 토큰은 이미 로그아웃 되었습니다."),

    // 로그인 오류
    USER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."),
    FAIL_TO_LOGIN_ERROR(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),
  
    // 질문 예외

    QUESTION_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "id에 해당하는 질문이 존재하지 않습니다."),
    NEITHER_BLANK_ERROR(HttpStatus.BAD_REQUEST, "제목과 내용은 빈 문자열일 수 없습니다."),

    // 토큰 오류
    AUTHORIZATION_ERROR(HttpStatus.UNAUTHORIZED, "인증, 인가 오류"),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "해당 토큰은 만료된 토큰입니다."),
    NULL_TOKEN(HttpStatus.UNAUTHORIZED, "access token 이 존재하지 않습니다."),
    TOKEN_BLACKLISTED(HttpStatus.BAD_REQUEST, "해당 토큰은 이미 로그아웃 되었습니다."),

    // 로그인 오류
    USER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."),
    FAIL_TO_LOGIN_ERROR(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),
  
    // 답변 예외
    ANSWER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "id에 해당하는 답변이 존재하지 않습니다."),

    // 해시태그 예외
    HASHTAG_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "id 에 해당하는 해시태그가 존재하지 않습니다."),
    HASHTAG_MAX_SIZE_5_ERROR(HttpStatus.BAD_REQUEST, "해시태그는 최대 5개까지 등록 가능합니다.");

    private final HttpStatus status;
    private final String message;
}
