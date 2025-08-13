package com.highlight.highlight_backend.dto;

import com.highlight.highlight_backend.domain.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 상품 수정 요청 DTO
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Getter
@NoArgsConstructor
public class ProductUpdateRequestDto {
    
    /**
     * 상품명
     */
    private String productName;
    
    /**
     * 상품 소개 (25자 제한)
     */
    private String shortDescription;
    
    /**
     * 상품 히스토리
     */
    private String history;
    
    /**
     * 기본 정보
     */
    private String basicInfo;
    
    /**
     * 기대효과
     */
    private String expectedEffects;
    
    /**
     * 상세 정보
     */
    private String detailedInfo;
    
    /**
     * 시작가
     */
    private BigDecimal startingPrice;
    
    /**
     * 입장료
     */
    private BigDecimal entranceFee;
    
    /**
     * 카테고리
     */
    private String category;
    
    /**
     * 상품 상태
     */
    private Product.ProductStatus status;
    
    /**
     * 상품 이미지 정보 목록 (전체 교체)
     */
    private List<ProductImageDto> images;
    
    /**
     * 상품 이미지 DTO
     */
    @Getter
    @NoArgsConstructor
    public static class ProductImageDto {
        /**
         * 이미지 ID (기존 이미지 수정시)
         */
        private Long id;
        
        /**
         * 이미지 URL
         */
        private String imageUrl;
        
        /**
         * 원본 파일명
         */
        private String originalFileName;
        
        /**
         * 파일 크기
         */
        private Long fileSize;
        
        /**
         * MIME 타입
         */
        private String mimeType;
        
        /**
         * 대표 이미지 여부
         */
        private boolean isPrimary = false;
        
        /**
         * 정렬 순서
         */
        private Integer sortOrder = 0;
        
        /**
         * 삭제 여부
         */
        private boolean isDeleted = false;
    }
}