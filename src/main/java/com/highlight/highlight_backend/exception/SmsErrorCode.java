package com.highlight.highlight_backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * SMS 인증 관련 에러 코드
 * 
 * @author 전우선
 * @since 2025.08.16
 */
@Getter
@RequiredArgsConstructor
public enum SmsErrorCode implements ErrorCode {
    
    /** SMS 전송 실패 */
    SMS_SEND_FAILED(HttpStatus.BAD_REQUEST, "SMS_001", "SMS 전송에 실패했습니다."),
    /** 유효하지 않은 인증번호 */
    VERIFICATION_CODE_NOT_FOUND(HttpStatus.UNAUTHORIZED, "SMS_002", "유효하지 않은 인증번호입니다."),
    /** 인증코드 유효시간 만료 */
    VERIFICATION_CODE_EXPIRED(HttpStatus.UNAUTHORIZED, "SMS_003", "인증 유효 시간이 초과되었습니다."),
    /** 휴대폰 인증 실패 또는 만료 */
    VERIFICATION_FAILED_OR_EXPIRED(HttpStatus.BAD_REQUEST, "SMS_004", "휴대폰 인증에 실패했거나 유효 시간이 만료되었습니다.");
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}