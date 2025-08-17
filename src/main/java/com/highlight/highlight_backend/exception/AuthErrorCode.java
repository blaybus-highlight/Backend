package com.highlight.highlight_backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 인증/인가 관련 에러 코드
 * 
 * @author 전우선
 * @since 2025.08.16
 */
@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    
    /** 유효하지 않은 인증 토큰 */
    INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "권한 정보가 없는 토큰입니다."),
    /** 인증되지 않은 사용자 */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_002", "인증되지 않은 사용자입니다."),
    /** 접근 거부 */
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_003", "권한이 없습니다.");
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}