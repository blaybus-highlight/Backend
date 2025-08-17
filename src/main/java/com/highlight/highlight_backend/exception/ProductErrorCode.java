package com.highlight.highlight_backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 상품 관련 에러 코드
 * 
 * @author 전우선
 * @since 2025.08.16
 */
@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    
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
    /** 이미지 업로드 실패 */
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PRODUCT_010", "이미지 업로드에 실패했습니다."),
    /** 이미지를 찾을 수 없음 */
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_011", "이미지를 찾을 수 없습니다."),
    /** 이미지가 상품에 속하지 않음 */
    IMAGE_NOT_BELONG_TO_PRODUCT(HttpStatus.BAD_REQUEST, "PRODUCT_012", "해당 이미지는 이 상품에 속하지 않습니다."),
    /** 이미지 파일 없음 */
    NO_IMAGE_FILES(HttpStatus.BAD_REQUEST, "PRODUCT_013", "업로드할 이미지 파일이 없습니다."),
    /** 이미지 파일 개수 초과 */
    TOO_MANY_IMAGE_FILES(HttpStatus.BAD_REQUEST, "PRODUCT_014", "이미지 파일은 최대 10개까지 업로드 가능합니다."),
    /** 빈 이미지 파일 */
    EMPTY_IMAGE_FILE(HttpStatus.BAD_REQUEST, "PRODUCT_015", "빈 이미지 파일은 업로드할 수 없습니다."),
    /** 이미지 파일 크기 초과 */
    IMAGE_FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "PRODUCT_016", "이미지 파일 크기는 5MB를 초과할 수 없습니다."),
    /** 유효하지 않은 이미지 파일 타입 */
    INVALID_IMAGE_FILE_TYPE(HttpStatus.BAD_REQUEST, "PRODUCT_017", "유효하지 않은 이미지 파일 형식입니다."),
    /** 지원하지 않는 이미지 파일 타입 */
    UNSUPPORTED_IMAGE_FILE_TYPE(HttpStatus.BAD_REQUEST, "PRODUCT_018", "지원하지 않는 이미지 파일 형식입니다.");
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}