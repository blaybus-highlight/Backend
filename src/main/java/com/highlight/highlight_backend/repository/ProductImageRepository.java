package com.highlight.highlight_backend.repository;

import com.highlight.highlight_backend.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 상품 이미지 Repository
 * 
 * 상품 이미지 데이터 액세스를 위한 JPA Repository입니다.
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    /**
     * 상품 ID로 이미지 목록 조회 (정렬 순서대로)
     * 
     * @param productId 상품 ID
     * @return 해당 상품의 이미지 목록
     */
    List<ProductImage> findByProductIdOrderBySortOrderAsc(Long productId);
    
    /**
     * 상품의 대표 이미지 조회
     * 
     * @param productId 상품 ID
     * @return 대표 이미지
     */
    Optional<ProductImage> findByProductIdAndIsPrimaryTrue(Long productId);
    
    /**
     * 상품 ID로 이미지 개수 조회
     * 
     * @param productId 상품 ID
     * @return 해당 상품의 이미지 개수
     */
    long countByProductId(Long productId);
    
    /**
     * 상품 ID로 모든 이미지 삭제
     * 
     * @param productId 상품 ID
     */
    void deleteByProductId(Long productId);
    
    /**
     * 파일명으로 이미지 조회
     * 
     * @param fileName 파일명
     * @return 해당 파일명의 이미지
     */
    Optional<ProductImage> findByFileName(String fileName);
    
    /**
     * 여러 상품 ID로 이미지 목록 조회
     * 
     * @param productIds 상품 ID 목록
     * @return 해당 상품들의 이미지 목록
     */
    List<ProductImage> findByProductIdIn(List<Long> productIds);
    
    /**
     * 여러 상품 ID로 이미지 URL만 조회
     * 
     * @param productIds 상품 ID 목록
     * @return 이미지 URL 목록
     */
    @Query("SELECT pi.imageUrl FROM ProductImage pi WHERE pi.product.id IN :productIds")
    List<String> findImageUrlsByProductIdIn(@Param("productIds") List<Long> productIds);
    
    /**
     * 단일 상품의 대표 이미지 URL 조회
     * 
     * @param productId 상품 ID
     * @return 대표 이미지 URL
     */
    @Query("SELECT pi.imageUrl FROM ProductImage pi WHERE pi.product.id = :productId AND pi.isPrimary = true")
    String findPrimaryImageUrlByProductId(@Param("productId") Long productId);
}