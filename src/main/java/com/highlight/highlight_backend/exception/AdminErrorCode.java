package com.highlight.highlight_backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 관리자 관련 에러 코드
 * 
 * @author 전우선
 * @since 2025.08.16
 */
@Getter
@RequiredArgsConstructor
public enum AdminErrorCode implements ErrorCode {
    
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
    /** 중복된 이메일 */
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "ADMIN_008", "이미 존재하는 이메일입니다.");
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}