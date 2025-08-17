package com.highlight.highlight_backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 공통 에러 코드
 * 
 * 애플리케이션 전반에서 사용되는 공통 에러 코드를 정의합니다.
 * 
 * @author 전우선
 * @since 2025.08.16
 */
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    
    /** 잘못된 입력값 */
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 입력값입니다."),
    /** 지원하지 않는 HTTP 메서드 */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_002", "지원하지 않는 HTTP 메서드입니다."),
    /** 엔티티를 찾을 수 없음 */
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_003", "엔티티를 찾을 수 없습니다."),
    /** 서버 내부 오류 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_004", "서버 오류가 발생했습니다."),
    /** WebSocket 연결 실패 */
    WEBSOCKET_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_005", "WebSocket 연결에 실패했습니다."),
    /** WebSocket 메시지 전송 실패 */
    WEBSOCKET_MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_006", "WebSocket 메시지 전송에 실패했습니다.");
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}