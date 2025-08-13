package com.highlight.highlight_backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 상품 엔티티
 * 
 * nafal 경매 시스템의 상품 정보를 저장하는 엔티티입니다.
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 상품명
     */
    @Column(nullable = false, length = 100)
    private String productName;
    
    /**
     * 상품 소개 (25자 제한)
     */
    @Column(nullable = false, length = 25)
    private String shortDescription;
    
    /**
     * 상품 히스토리
     */
    @Column(columnDefinition = "TEXT")
    private String history;
    
    /**
     * 기본 정보
     */
    @Column(columnDefinition = "TEXT")
    private String basicInfo;
    
    /**
     * 기대효과
     */
    @Column(columnDefinition = "TEXT")
    private String expectedEffects;
    
    /**
     * 상세 정보
     */
    @Column(columnDefinition = "TEXT")
    private String detailedInfo;
    
    /**
     * 시작가
     */
    @Column(nullable = false, precision = 15, scale = 0)
    private BigDecimal startingPrice;
    
    /**
     * 입장료
     */
    @Column(nullable = false, precision = 15, scale = 0)
    private BigDecimal entranceFee;
    
    /**
     * 상품 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.DRAFT;
    
    /**
     * 카테고리
     */
    @Column(length = 50)
    private String category;
    
    /**
     * 등록한 관리자 ID
     */
    @Column(nullable = false)
    private Long registeredBy;
    
    /**
     * 상품 이미지 목록
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductImage> images = new ArrayList<>();
    
    /**
     * 생성 시간
     */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 수정 시간
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    /**
     * 상품 상태 열거형
     */
    public enum ProductStatus {
        DRAFT("임시저장"),
        ACTIVE("활성"),
        INACTIVE("비활성"),
        AUCTION_READY("경매대기"),
        IN_AUCTION("경매중"),
        AUCTION_COMPLETED("경매완료");
        
        private final String description;
        
        ProductStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 상품 이미지 추가
     */
    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }
    
    /**
     * 상품 이미지 제거
     */
    public void removeImage(ProductImage image) {
        images.remove(image);
        image.setProduct(null);
    }
    
    /**
     * 대표 이미지 조회
     */
    public ProductImage getPrimaryImage() {
        return images.stream()
            .filter(ProductImage::isPrimary)
            .findFirst()
            .orElse(images.isEmpty() ? null : images.get(0));
    }
}