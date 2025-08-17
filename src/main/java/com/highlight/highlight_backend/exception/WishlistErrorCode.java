package com.highlight.highlight_backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 찜하기 관련 에러 코드
 * 
 * @author 전우선
 * @since 2025.08.16
 */
@Getter
@RequiredArgsConstructor
public enum WishlistErrorCode implements ErrorCode {
    
    /** 중복된 찜하기 */
    DUPLICATE_WISHLIST(HttpStatus.CONFLICT, "WISHLIST_001", "이미 찜한 상품입니다."),
    /** 찜하기를 찾을 수 없음 */
    WISHLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "WISHLIST_002", "찜하기를 찾을 수 없습니다.");
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}