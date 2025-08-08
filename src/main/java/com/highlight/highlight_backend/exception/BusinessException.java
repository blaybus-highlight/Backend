package com.highlight.highlight_backend.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 예외
 * 
 * 비즈니스 규칙에 위반되거나 비즈니스 처리 중 발생하는 예외를 나타냅니다.
 * ErrorCode와 함께 사용되어 상세한 에러 정보를 제공합니다.
 *
 * @author 전우선
 * @since 2025.08.08
 */
@Getter
public class BusinessException extends RuntimeException {
    
    /**
     * 예외에 대한 상세 정보를 포함하는 에러 코드
     */
    private final ErrorCode errorCode;
    
    /**
     * 에러 코드를 사용하여 비즈니스 예외를 생성합니다.
     * 
     * @param errorCode 에러 코드
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    /**
     * 에러 코드와 원인 예외를 사용하여 비즈니스 예외를 생성합니다.
     * 
     * @param errorCode 에러 코드
     * @param cause 원인 예외
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}