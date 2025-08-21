package com.highlight.highlight_backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 결제 관련 에러 코드
 * 
 * @author 탁찬홍
 * @since 2025.08.21
 */
@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    
    PAYMENT_NOT_FOUND("P001", "결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INSUFFICIENT_POINT("P002", "보유 포인트가 부족합니다.", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_AMOUNT("P003", "잘못된 결제 금액입니다.", HttpStatus.BAD_REQUEST),
    AUCTION_NOT_WON("P004", "낙찰하지 않은 경매입니다.", HttpStatus.FORBIDDEN),
    PAYMENT_ALREADY_COMPLETED("P005", "이미 결제가 완료된 경매입니다.", HttpStatus.CONFLICT),
    AUCTION_NOT_ENDED("P006", "아직 종료되지 않은 경매입니다.", HttpStatus.BAD_REQUEST),
    INVALID_POINT_USAGE("P007", "포인트 사용 금액이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_PROCESSING_FAILED("P008", "결제 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
    
    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    
    @Override
    public String getCode() {
        return code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}
