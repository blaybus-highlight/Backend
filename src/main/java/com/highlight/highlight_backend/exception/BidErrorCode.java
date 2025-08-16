package com.highlight.highlight_backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 입찰 관련 에러 코드
 * 
 * @author 전우선
 * @since 2025.08.16
 */
@Getter
@RequiredArgsConstructor
public enum BidErrorCode implements ErrorCode {
    
    /** 입찰을 찾을 수 없음 */
    BID_NOT_FOUND(HttpStatus.NOT_FOUND, "BID_001", "입찰을 찾을 수 없습니다."),
    /** 경매가 종료되지 않음 */
    AUCTION_NOT_ENDED(HttpStatus.BAD_REQUEST, "BID_002", "경매가 아직 종료되지 않았습니다.");
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}