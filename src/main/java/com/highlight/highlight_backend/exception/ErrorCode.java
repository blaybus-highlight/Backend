package com.highlight.highlight_backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 열거형
 * 
 * 애플리케이션에서 발생할 수 있는 모든 에러에 대한 코드와 메시지를 정의합니다.
 * HTTP 상태 코드, 에러 코드, 에러 메시지를 포함합니다.
 *
 * @author 전우선
 * @since 2025.08.08
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // ===== 공통 에러 =====
    /** 잘못된 입력값 */
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 입력값입니다."),
    /** 지원하지 않는 HTTP 메서드 */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_002", "지원하지 않는 HTTP 메서드입니다."),
    /** 엔티티를 찾을 수 없음 */
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_003", "엔티티를 찾을 수 없습니다."),
    /** 서버 내부 오류 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_004", "서버 오류가 발생했습니다."),
    
    
    // ===== 인증/인가 에러 =====
    /** 유효하지 않은 인증 토큰 */
    INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "권한 정보가 없는 토큰입니다."),
    /** 인증되지 않은 사용자 */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_002", "인증되지 않은 사용자입니다."),
    /** 접근 기업 */
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_003", "권한이 없습니다."),
    
    
    // ===== 사용자 관련 에러 =====
    /** 사용자를 찾을 수 없음 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다."),
    /** 이미 존재하는 이메일 */
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_002", "이미 존재하는 이메일입니다.");
    
    /**
     * HTTP 상태 코드
     */
    private final HttpStatus httpStatus;
    
    /**
     * 에러 코드 (애플리케이션 내부용)
     */
    private final String code;
    
    /**
     * 에러 메시지 (사용자에게 노출되는 메시지)
     */
    private final String message;
}