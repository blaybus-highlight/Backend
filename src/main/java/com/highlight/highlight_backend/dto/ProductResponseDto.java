package com.highlight.highlight_backend.dto;

import com.highlight.highlight_backend.domain.Product;
import com.highlight.highlight_backend.domain.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 상품 응답 DTO
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Getter
@AllArgsConstructor
public class ProductResponseDto {
    
    /**
     * 상품 ID
     */
    private Long id;
    
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
    //private String basicInfo;
    
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
    //private BigDecimal entranceFee;
    
    /**
     * 상품 상태
     */
    private Product.ProductStatus status;
    
    /**
     * 상품 상태 설명
     */
    private String statusDescription;
    
    /**
     * 카테고리
     */
    private Product.Category category;
    
    /**
     * 등록한 관리자 ID
     */
    private Long registeredBy;
    
    /**
     * 상품 이미지 목록
     */
    private List<ProductImageResponseDto> images;
    
    /**
     * 대표 이미지 URL
     */
    private String primaryImageUrl;
    
    /**
     * 프리미엄 상품 여부
     */
    private Boolean isPremium;
    
    /**
     * 생성 시간
     */
    private LocalDateTime createdAt;
    
    /**
     * 수정 시간
     */
    private LocalDateTime updatedAt;
    
    /**
     * Product 엔티티로부터 DTO 생성
     */
    public static ProductResponseDto from(Product product) {
        List<ProductImageResponseDto> imageDtos = product.getImages().stream()
            .map(ProductImageResponseDto::from)
            .collect(Collectors.toList());
        
        String primaryImageUrl = product.getPrimaryImage() != null ? 
            product.getPrimaryImage().getImageUrl() : null;
        
        return new ProductResponseDto(
            product.getId(),
            product.getProductName(),
            product.getShortDescription(),
            product.getHistory(),
            product.getExpectedEffects(),
            product.getDetailedInfo(),
            product.getStartingPrice(),
            product.getStatus(),
            product.getStatus().getDescription(),
            product.getCategory(),
            product.getRegisteredBy(),
            imageDtos,
            primaryImageUrl,
            product.getIsPremium(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
    
    /**
     * 상품 이미지 응답 DTO
     */
    @Getter
    @AllArgsConstructor
    public static class ProductImageResponseDto {
        /**
         * 이미지 ID
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
        private boolean isPrimary;
        
        /**
         * 정렬 순서
         */
        private Integer sortOrder;
        
        /**
         * 생성 시간
         */
        private LocalDateTime createdAt;
        
        /**
         * ProductImage 엔티티로부터 DTO 생성
         */
        public static ProductImageResponseDto from(ProductImage image) {
            return new ProductImageResponseDto(
                image.getId(),
                image.getImageUrl(),
                image.getOriginalFileName(),
                image.getFileSize(),
                image.getMimeType(),
                image.isPrimary(),
                image.getSortOrder(),
                image.getCreatedAt()
            );
        }
    }
}