package com.highlight.highlight_backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 판매자 관련 에러 코드
 * 
 * @author 전우선
 * @since 2025.08.16
 */
@Getter
@RequiredArgsConstructor
public enum SellerErrorCode implements ErrorCode {
    
    /** 판매자를 찾을 수 없음 */
    SELLER_NOT_FOUND(HttpStatus.NOT_FOUND, "SELLER_001", "판매자를 찾을 수 없습니다.");
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}