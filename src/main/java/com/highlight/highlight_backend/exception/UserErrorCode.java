package com.highlight.highlight_backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 사용자 관련 에러 코드
 * 
 * @author 전우선
 * @since 2025.08.16
 */
@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    
    /** 사용자를 찾을 수 없음 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다."),
    /** 이미 존재하는 이메일 */
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_002", "이미 존재하는 이메일입니다."),
    /** 이미 존재하는 휴대번호 */
    PHONE_NUMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_003", "이미 존재하는 휴대번호입니다."),
    /** 휴대폰 인증이 필요한 경우 */
    VERIFICATION_REQUIRED(HttpStatus.BAD_REQUEST, "USER_004", "휴대폰 인증이 필요합니다."),
    /** 중복된 사용자 ID */
    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "USER_005", "이미 사용 중인 아이디입니다."),
    /** 중복된 닉네임 */
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "USER_006", "이미 사용 중인 닉네임입니다."),
    /** 중복된 휴대폰 번호 */
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "USER_007", "이미 사용 중인 휴대폰 번호입니다."),
    /** 유효하지 않은 휴대폰 번호 */
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "USER_008", "유효하지 않은 휴대폰 번호 형식입니다."),
    /** 인증번호 불일치 */
    VERIFICATION_CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER_009", "인증번호가 일치하지 않습니다."),
    /** 잘못된 로그인 정보 */
    INVALID_LOGIN_CREDENTIALS(HttpStatus.UNAUTHORIZED, "USER_010", "아이디 또는 비밀번호가 올바르지 않습니다.");
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}