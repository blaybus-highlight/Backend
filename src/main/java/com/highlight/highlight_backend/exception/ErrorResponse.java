package com.highlight.highlight_backend.exception;

import lombok.Builder;
import lombok.Getter;

/**
 * 에러 응답 DTO
 * 
 * 클라이언트에게 전송될 에러 정보를 담는 객체입니다.
 * 일관된 에러 응답 형식을 제공하여 API 사용성을 향상시킵니다.
 *
 * @author 전우선
 * @since 2025.08.08
 */
@Getter
@Builder
public class ErrorResponse {
    
    /**
     * 에러 코드
     * 애플리케이션에서 정의한 고유한 에러 식별자
     */
    private String code;
    
    /**
     * 에러 메시지
     * 사용자에게 표시될 에러 설명
     */
    private String message;
}