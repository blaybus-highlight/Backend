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
    CANNOT_DELETE_AUCTION_PRODUCT(HttpStatus.BAD_REQUEST, "PRODUCT_003", "경매 중인 상품은 삭제할 수 없습니다."),
    /** 유효하지 않은 상품 갯수 */
    INVALID_PRODUCT_COUNT(HttpStatus.BAD_REQUEST, "PRODUCT_004", "상품 갯수는 1개 이상이어야 합니다."),
    /** 유효하지 않은 제조년도 */
    INVALID_MANUFACTURE_YEAR(HttpStatus.BAD_REQUEST, "PRODUCT_005", "제조년도가 유효하지 않습니다."),
    /** 재질 정보 누락 */
    INVALID_MATERIAL(HttpStatus.BAD_REQUEST, "PRODUCT_006", "상품 재질 정보는 필수입니다."),
    /** 사이즈 정보 누락 */
    INVALID_SIZE(HttpStatus.BAD_REQUEST, "PRODUCT_007", "상품 사이즈 정보는 필수입니다."),
    /** 브랜드 정보 누락 */
    INVALID_BRAND(HttpStatus.BAD_REQUEST, "PRODUCT_008", "브랜드 정보는 필수입니다."),
    /** 상품 등급 누락 */
    INVALID_PRODUCT_RANK(HttpStatus.BAD_REQUEST, "PRODUCT_009", "상품 등급 정보는 필수입니다."),
    
    
    // ===== 경매 관련 에러 =====
    /** 경매를 찾을 수 없음 */
    AUCTION_NOT_FOUND(HttpStatus.NOT_FOUND, "AUCTION_001", "경매를 찾을 수 없습니다."),
    /** 상품이 이미 경매에 등록됨 */
    PRODUCT_ALREADY_IN_AUCTION(HttpStatus.CONFLICT, "AUCTION_002", "이미 경매에 등록된 상품입니다."),
    /** 경매 가능하지 않은 상품 상태 */
    INVALID_PRODUCT_STATUS_FOR_AUCTION(HttpStatus.BAD_REQUEST, "AUCTION_003", "경매 가능한 상품 상태가 아닙니다."),
    /** 경매 시작 불가 */
    CANNOT_START_AUCTION(HttpStatus.BAD_REQUEST, "AUCTION_004", "경매를 시작할 수 없는 상태입니다."),
    /** 경매 종료 불가 */
    CANNOT_END_AUCTION(HttpStatus.BAD_REQUEST, "AUCTION_005", "경매를 종료할 수 없는 상태입니다."),
    /** 유효하지 않은 경매 시작 시간 */
    INVALID_AUCTION_START_TIME(HttpStatus.BAD_REQUEST, "AUCTION_006", "경매 시작 시간이 유효하지 않습니다."),
    /** 유효하지 않은 경매 종료 시간 */
    INVALID_AUCTION_END_TIME(HttpStatus.BAD_REQUEST, "AUCTION_007", "경매 종료 시간이 유효하지 않습니다."),
    /** 경매 진행 시간 부족 */
    AUCTION_DURATION_TOO_SHORT(HttpStatus.BAD_REQUEST, "AUCTION_008", "경매 진행 시간은 최소 10분 이상이어야 합니다."),
    /** 유효하지 않은 입찰 단위 */
    INVALID_BID_UNIT(HttpStatus.BAD_REQUEST, "AUCTION_009", "입찰 단위는 0보다 커야 합니다."),
    /** 유효하지 않은 배송비 */
    INVALID_SHIPPING_FEE(HttpStatus.BAD_REQUEST, "AUCTION_010", "배송비는 0원 이상이어야 합니다."),
    /** 유효하지 않은 최소 입찰가 */
    INVALID_MINIMUM_BID(HttpStatus.BAD_REQUEST, "AUCTION_011", "최소 입찰가는 입찰 단위보다 크거나 같아야 합니다."),
    /** 입찰가와 입찰 단위 불일치 */
    BID_UNIT_MISMATCH(HttpStatus.BAD_REQUEST, "AUCTION_012", "입찰가는 입찰 단위의 배수여야 합니다."),
    /** 최대 입찰가가 최소 입찰가보다 작음 */
    MAX_BID_LESS_THAN_MIN_BID(HttpStatus.BAD_REQUEST, "AUCTION_013", "최대 입찰가는 최소 입찰가보다 크거나 같아야 합니다."),


    // ===== User 회원가입 관련 에러 ===== //
    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "USER_001", "이미 사용 중인 아이디입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "USER_002", "이미 사용 중인 닉네임입니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "USER_003", "이미 사용 중인 휴대폰 번호입니다."),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "USER_004", "유효하지 않은 휴대폰 번호 형식입니다."),
    VERIFICATION_CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER_005", "인증번호가 일치하지 않습니다."),


    // ===== 판매자 관련 에러 =====
    /** 판매자를 찾을 수 없음 */
    SELLER_NOT_FOUND(HttpStatus.NOT_FOUND, "SELLER_001", "판매자를 찾을 수 없습니다."),
    /** 중복된 판매자 이메일 */
    DUPLICATE_SELLER_EMAIL(HttpStatus.CONFLICT, "SELLER_002", "이미 존재하는 판매자 이메일입니다."),
    /** 중복된 사업자 등록번호 */
    DUPLICATE_BUSINESS_NUMBER(HttpStatus.CONFLICT, "SELLER_003", "이미 존재하는 사업자 등록번호입니다."),
    /** 비활성화된 판매자 */
    INACTIVE_SELLER(HttpStatus.FORBIDDEN, "SELLER_004", "비활성화된 판매자입니다."),
    /** 정지된 판매자 */
    SUSPENDED_SELLER(HttpStatus.FORBIDDEN, "SELLER_005", "정지된 판매자입니다."),


    // ===== 입찰 관련 에러 =====
    /** 입찰을 찾을 수 없음 */
    BID_NOT_FOUND(HttpStatus.NOT_FOUND, "BID_001", "입찰을 찾을 수 없습니다."),
    /** 유효하지 않은 입찰 금액 */
    INVALID_BID_AMOUNT(HttpStatus.BAD_REQUEST, "BID_002", "입찰 금액이 유효하지 않습니다."),
    /** 입찰 금액이 현재가보다 낮음 */
    BID_AMOUNT_TOO_LOW(HttpStatus.BAD_REQUEST, "BID_003", "입찰 금액이 현재 최고가보다 낮습니다."),
    /** 본인 입찰에 재입찰 */
    CANNOT_BID_ON_OWN_BID(HttpStatus.BAD_REQUEST, "BID_004", "본인의 입찰에는 재입찰할 수 없습니다."),
    /** 경매 종료로 입찰 불가 */
    AUCTION_ENDED_CANNOT_BID(HttpStatus.BAD_REQUEST, "BID_005", "종료된 경매에는 입찰할 수 없습니다."),
    /** 경매 시작 전 입찰 불가 */
    AUCTION_NOT_STARTED_CANNOT_BID(HttpStatus.BAD_REQUEST, "BID_006", "시작되지 않은 경매에는 입찰할 수 없습니다."),
    /** 입찰 취소 불가 */
    CANNOT_CANCEL_BID(HttpStatus.BAD_REQUEST, "BID_007", "입찰을 취소할 수 없습니다."),


    // ===== WebSocket 관련 에러 =====
    /** WebSocket 연결 실패 */
    WEBSOCKET_CONNECTION_FAILED(HttpStatus.BAD_REQUEST, "WEBSOCKET_001", "WebSocket 연결에 실패했습니다."),
    /** WebSocket 메시지 전송 실패 */
    WEBSOCKET_MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "WEBSOCKET_002", "실시간 메시지 전송에 실패했습니다."),
    /** 유효하지 않은 WebSocket 메시지 형식 */
    INVALID_WEBSOCKET_MESSAGE_FORMAT(HttpStatus.BAD_REQUEST, "WEBSOCKET_003", "유효하지 않은 메시지 형식입니다."),
    /** WebSocket 구독 실패 */
    WEBSOCKET_SUBSCRIPTION_FAILED(HttpStatus.BAD_REQUEST, "WEBSOCKET_004", "실시간 알림 구독에 실패했습니다."),
    /** WebSocket 연결 해제 오류 */
    WEBSOCKET_DISCONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "WEBSOCKET_005", "연결 해제 중 오류가 발생했습니다."),
    /** 경매 구독 권한 없음 */
    WEBSOCKET_AUCTION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "WEBSOCKET_006", "해당 경매의 실시간 정보에 접근할 권한이 없습니다."),;

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