package com.highlight.highlight_backend.exception;

import org.springframework.http.HttpStatus;

/**
 * 에러 코드 인터페이스
 * 
 * 모든 도메인별 에러 코드가 구현해야 하는 공통 인터페이스입니다.
 * 
 * @author 전우선
 * @since 2025.08.16
 */
public interface ErrorCode {
    
    /**
     * HTTP 상태 코드를 반환합니다.
     * 
     * @return HTTP 상태 코드
     */
    HttpStatus getHttpStatus();
    
    /**
     * 에러 코드를 반환합니다.
     * 
     * @return 에러 코드
     */
    String getCode();
    
    /**
     * 에러 메시지를 반환합니다.
     * 
     * @return 에러 메시지
     */
    String getMessage();
}