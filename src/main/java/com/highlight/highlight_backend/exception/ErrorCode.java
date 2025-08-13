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
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_002", "이미 존재하는 이메일입니다."),
    
    
    // ===== 관리자 관련 에러 =====
    /** 관리자를 찾을 수 없음 */
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "ADMIN_001", "관리자를 찾을 수 없습니다."),
    /** 잘못된 로그인 정보 */
    INVALID_LOGIN_CREDENTIALS(HttpStatus.UNAUTHORIZED, "ADMIN_002", "아이디 또는 비밀번호가 올바르지 않습니다."),
    /** 비활성화된 관리자 계정 */
    INACTIVE_ADMIN_ACCOUNT(HttpStatus.FORBIDDEN, "ADMIN_003", "비활성화된 관리자 계정입니다."),
    /** 중복된 관리자 ID */
    DUPLICATE_ADMIN_ID(HttpStatus.CONFLICT, "ADMIN_004", "이미 존재하는 관리자 ID입니다."),
    /** 권한 부족 */
    INSUFFICIENT_PERMISSION(HttpStatus.FORBIDDEN, "ADMIN_005", "해당 작업을 수행할 권한이 없습니다."),
    /** 본인 계정 비활성화 불가 */
    CANNOT_DEACTIVATE_SELF(HttpStatus.BAD_REQUEST, "ADMIN_006", "본인 계정은 비활성화할 수 없습니다."),
    /** 본인 계정 삭제 불가 */
    CANNOT_DELETE_SELF(HttpStatus.BAD_REQUEST, "ADMIN_007", "본인 계정은 삭제할 수 없습니다."),
    
    
    // ===== 상품 관련 에러 =====
    /** 상품을 찾을 수 없음 */
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_001", "상품을 찾을 수 없습니다."),
    /** 상품 소개 길이 초과 */
    INVALID_PRODUCT_DESCRIPTION_LENGTH(HttpStatus.BAD_REQUEST, "PRODUCT_002", "상품 소개는 25자를 초과할 수 없습니다."),
    /** 경매 중인 상품 삭제 불가 */
    CANNOT_DELETE_AUCTION_PRODUCT(HttpStatus.BAD_REQUEST, "PRODUCT_003", "경매 중인 상품은 삭제할 수 없습니다.");
    
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