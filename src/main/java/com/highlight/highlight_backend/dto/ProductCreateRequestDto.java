package com.highlight.highlight_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 상품 등록 요청 DTO
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Getter
@NoArgsConstructor
public class ProductCreateRequestDto {
    
    /**
     * 상품명
     */
    @NotBlank(message = "상품명은 필수입니다")
    @Size(max = 100, message = "상품명은 100자를 초과할 수 없습니다")
    private String productName;
    
    /**
     * 상품 소개 (25자 제한)
     */
    @NotBlank(message = "상품 소개는 필수입니다")
    @Size(max = 25, message = "상품 소개는 25자를 초과할 수 없습니다")
    private String shortDescription;
    
    /**
     * 상품 히스토리
     */
    @Size(max = 2000, message = "히스토리는 2000자를 초과할 수 없습니다")
    private String history;
    
    /**
     * 기본 정보
     */
    @Size(max = 2000, message = "기본 정보는 2000자를 초과할 수 없습니다")
    private String basicInfo;
    
    /**
     * 기대효과
     */
    @Size(max = 2000, message = "기대효과는 2000자를 초과할 수 없습니다")
    private String expectedEffects;
    
    /**
     * 상세 정보
     */
    @Size(max = 5000, message = "상세 정보는 5000자를 초과할 수 없습니다")
    private String detailedInfo;
    
    /**
     * 시작가
     */
    @NotNull(message = "시작가는 필수입니다")
    @DecimalMin(value = "0", message = "시작가는 0원 이상이어야 합니다")
    @DecimalMax(value = "999999999999999", message = "시작가가 너무 큽니다")
    private BigDecimal startingPrice;
    
    /**
     * 입장료
     */
    @NotNull(message = "입장료는 필수입니다")
    @DecimalMin(value = "0", message = "입장료는 0원 이상이어야 합니다")
    @DecimalMax(value = "999999999999999", message = "입장료가 너무 큽니다")
    private BigDecimal entranceFee;
    
    /**
     * 카테고리
     */
    @Size(max = 50, message = "카테고리는 50자를 초과할 수 없습니다")
    private String category;
    
    /**
     * 상품 이미지 정보 목록
     */
    @Valid
    private List<ProductImageDto> images;
    
    /**
     * 임시저장 여부 (true: 임시저장, false: 활성 상태로 등록)
     */
    private boolean isDraft = false;
    
    /**
     * 상품 이미지 DTO
     */
    @Getter
    @NoArgsConstructor
    public static class ProductImageDto {
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
    }
}