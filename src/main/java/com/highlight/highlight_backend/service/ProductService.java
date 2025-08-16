package com.highlight.highlight_backend.service;

import com.highlight.highlight_backend.domain.Admin;
import com.highlight.highlight_backend.domain.Product;
import com.highlight.highlight_backend.domain.ProductImage;
import com.highlight.highlight_backend.dto.ProductCreateRequestDto;
import com.highlight.highlight_backend.dto.ProductResponseDto;
import com.highlight.highlight_backend.dto.ProductUpdateRequestDto;
import com.highlight.highlight_backend.exception.BusinessException;
import com.highlight.highlight_backend.exception.ErrorCode;
import com.highlight.highlight_backend.repository.AdminRepository;
import com.highlight.highlight_backend.repository.ProductImageRepository;
import com.highlight.highlight_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 상품 관리 서비스
 * 
 * 경매 진행 상품의 등록, 수정, 조회 기능을 제공합니다.
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final AdminRepository adminRepository;
    private final S3Service s3Service;
    
    /**
     * 상품 등록
     * 
     * @param request 상품 등록 요청 데이터
     * @param adminId 등록하는 관리자 ID
     * @return 등록된 상품 정보
     */
    @Transactional
    public ProductResponseDto createProduct(ProductCreateRequestDto request, Long adminId) {
        log.info("상품 등록 요청: {} (관리자: {})", request.getProductName(), adminId);
        
        // 1. 관리자 권한 확인
        Admin admin = validateProductManagePermission(adminId);
        
        // 2. 상품 소개 글자 수 검증
        if (StringUtils.hasText(request.getShortDescription()) && 
            request.getShortDescription().length() > 25) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_DESCRIPTION_LENGTH);
        }
        
        // 3. 상품 정보 검증
        validateProductData(request);
        
        // 4. 상품 엔티티 생성
        Product product = new Product();
        product.setProductName(request.getProductName());
        product.setShortDescription(request.getShortDescription());
        product.setHistory(request.getHistory());
        product.setExpectedEffects(request.getExpectedEffects());
        product.setDetailedInfo(request.getDetailedInfo());
        product.setCategory(request.getCategory());
        product.setProductCount(request.getProductCount());
        product.setMaterial(request.getMaterial());
        product.setSize(request.getSize());
        product.setBrand(request.getBrand());
        product.setManufactureYear(request.getManufactureYear());
        product.setCondition(request.getCondition());
        product.setRank(request.getRank());
        product.setRegisteredBy(adminId);
        product.setSellerId(1L); // 고정 판매자 NAFAL (ID=1)
        
        // 상태 설정 (임시저장 또는 활성)
        product.setStatus(request.isDraft() ? Product.ProductStatus.DRAFT : Product.ProductStatus.ACTIVE);
        
        // 4. 상품 저장
        Product savedProduct = productRepository.save(product);
        
        // 5. 상품 이미지 처리
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            processProductImages(savedProduct, request.getImages());
        }
        
        log.info("상품 등록 완료: {} (ID: {})", savedProduct.getProductName(), savedProduct.getId());
        
        return ProductResponseDto.from(savedProduct);
    }
    
    /**
     * 상품 수정
     * 
     * @param productId 수정할 상품 ID
     * @param request 상품 수정 요청 데이터
     * @param adminId 수정하는 관리자 ID
     * @return 수정된 상품 정보
     */
    @Transactional
    public ProductResponseDto updateProduct(Long productId, ProductUpdateRequestDto request, Long adminId) {
        log.info("상품 수정 요청: {} (관리자: {})", productId, adminId);
        
        // 1. 관리자 권한 확인
        validateProductManagePermission(adminId);
        
        // 2. 상품 조회
        Product product = productRepository.findByIdWithImages(productId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        
        // 3. 상품 소개 글자 수 검증
        if (StringUtils.hasText(request.getShortDescription()) && 
            request.getShortDescription().length() > 25) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_DESCRIPTION_LENGTH);
        }
        
        // 3.5. 상품 데이터 검증
        validateProductUpdateData(request);
        
        // 4. 상품 정보 업데이트
        if (StringUtils.hasText(request.getProductName())) {
            product.setProductName(request.getProductName());
        }
        if (StringUtils.hasText(request.getShortDescription())) {
            product.setShortDescription(request.getShortDescription());
        }
        if (request.getHistory() != null) {
            product.setHistory(request.getHistory());
        }
        if (request.getExpectedEffects() != null) {
            product.setExpectedEffects(request.getExpectedEffects());
        }
        if (request.getDetailedInfo() != null) {
            product.setDetailedInfo(request.getDetailedInfo());
        }
        if (request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }
        if (request.getProductCount() != null) {
            product.setProductCount(request.getProductCount());
        }
        if (request.getMaterial() != null) {
            product.setMaterial(request.getMaterial());
        }
        if (request.getSize() != null) {
            product.setSize(request.getSize());
        }
        if (request.getBrand() != null) {
            product.setBrand(request.getBrand());
        }
        if (request.getManufactureYear() != null) {
            product.setManufactureYear(request.getManufactureYear());
        }
        if (request.getCondition() != null) {
            product.setCondition(request.getCondition());
        }
        if (request.getRank() != null) {
            product.setRank(request.getRank());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        
        // 5. 상품 이미지 업데이트
        if (request.getImages() != null) {
            updateProductImages(product, request.getImages());
        }
        
        Product updatedProduct = productRepository.save(product);
        
        log.info("상품 수정 완료: {} (ID: {})", updatedProduct.getProductName(), updatedProduct.getId());
        
        return ProductResponseDto.from(updatedProduct);
    }
    
    /**
     * 상품 목록 조회
     * 
     * @param pageable 페이징 정보
     * @param adminId 조회하는 관리자 ID
     * @return 상품 목록
     */
    public Page<ProductResponseDto> getProductList(Pageable pageable, Long adminId) {
        log.info("상품 목록 조회 요청 (관리자: {})", adminId);
        
        validateProductManagePermission(adminId);
        
        return productRepository.findAll(pageable)
            .map(ProductResponseDto::from);
    }
    
    /**
     * 상품 상세 조회
     * 
     * @param productId 조회할 상품 ID
     * @param adminId 조회하는 관리자 ID
     * @return 상품 상세 정보
     */
    public ProductResponseDto getProduct(Long productId, Long adminId) {
        log.info("상품 상세 조회 요청: {} (관리자: {})", productId, adminId);
        
        validateProductManagePermission(adminId);
        
        Product product = productRepository.findByIdWithImages(productId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        
        return ProductResponseDto.from(product);
    }
    
    /**
     * 상품 삭제
     * 
     * @param productId 삭제할 상품 ID
     * @param adminId 삭제하는 관리자 ID
     */
    @Transactional
    public void deleteProduct(Long productId, Long adminId) {
        log.info("상품 삭제 요청: {} (관리자: {})", productId, adminId);
        
        validateProductManagePermission(adminId);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        
        // 경매 중인 상품은 삭제 불가
        if (product.getStatus() == Product.ProductStatus.IN_AUCTION) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_AUCTION_PRODUCT);
        }
        
        productRepository.delete(product);
        
        log.info("상품 삭제 완료: {} (ID: {})", product.getProductName(), product.getId());
    }

    /**
     * 관련 상품 추천 조회
     * 
     * @param productId 기준 상품 ID
     * @param size 추천 상품 개수
     * @return 추천 상품 목록
     */
    public Page<ProductResponseDto> getRecommendedProducts(Long productId, int size) {
        log.info("관련 상품 추천 조회: {} (개수: {})", productId, size);
        
        // 1. 기준 상품 조회
        Product baseProduct = productRepository.findById(productId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        
        // 2. 추천 로직: 동일 카테고리 또는 동일 브랜드 상품 조회
        List<Product> recommendedProducts = productRepository.findRecommendedProducts(
            productId, 
            baseProduct.getCategory(), 
            baseProduct.getBrand(),
            PageRequest.of(0, size)
        );
        
        // 3. DTO 변환
        List<ProductResponseDto> responseDtos = recommendedProducts.stream()
            .map(ProductResponseDto::from)
            .toList();
        
        // 4. Page 객체로 변환
        return new PageImpl<>(responseDtos, PageRequest.of(0, size), responseDtos.size());
    }
    
    /**
     * 상품 관리 권한 검증
     * 
     * @param adminId 검증할 관리자 ID
     * @return 검증된 관리자 정보
     */
    private Admin validateProductManagePermission(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));
        
        // 기획 변경: 모든 관리자가 상품 관리 가능, SUPER_ADMIN 체크만 유지
        if (admin.getRole() != Admin.AdminRole.SUPER_ADMIN && admin.getRole() != Admin.AdminRole.ADMIN) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_PERMISSION);
        }
        
        return admin;
    }
    
    /**
     * 상품 프리미엄 설정 변경
     * 
     * @param productId 상품 ID
     * @param isPremium 프리미엄 설정 여부
     * @param adminId 관리자 ID
     * @return 업데이트된 상품 정보
     */
    @Transactional
    public ProductResponseDto updateProductPremium(Long productId, Boolean isPremium, Long adminId) {
        log.info("상품 프리미엄 설정 변경: 상품={}, 프리미엄={}, 관리자={}", productId, isPremium, adminId);
        
        // 관리자 권한 검증
        validateProductManagePermission(adminId);
        
        // 상품 조회
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        
        // 프리미엄 설정 변경
        product.setIsPremium(isPremium);
        
        // 저장
        Product savedProduct = productRepository.save(product);
        
        log.info("상품 프리미엄 설정 완료: {} (ID: {}, 프리미엄: {})", 
                savedProduct.getProductName(), savedProduct.getId(), isPremium);
        
        return ProductResponseDto.from(savedProduct);
    }
    
    /**
     * 상품 이미지 처리 (신규 등록)
     * 
     * @param product 상품 엔티티
     * @param imageDtos 이미지 DTO 목록
     */
    private void processProductImages(Product product, List<ProductCreateRequestDto.ProductImageDto> imageDtos) {
        for (ProductCreateRequestDto.ProductImageDto imageDto : imageDtos) {
            String fileName = generateFileName(imageDto.getOriginalFileName());
            
            ProductImage image = new ProductImage(
                fileName,
                imageDto.getOriginalFileName(),
                imageDto.getImageUrl(),
                imageDto.getFileSize(),
                imageDto.getMimeType()
            );
            
            image.setPrimary(imageDto.isPrimary());
            image.setSortOrder(imageDto.getSortOrder());
            
            product.addImage(image);
        }
    }
    
    /**
     * 상품 이미지 업데이트 (수정)
     * 
     * @param product 상품 엔티티
     * @param imageDtos 이미지 DTO 목록
     */
    private void updateProductImages(Product product, List<ProductUpdateRequestDto.ProductImageDto> imageDtos) {
        // 기존 이미지 모두 제거
        product.getImages().clear();
        
        // 새로운 이미지 추가
        for (ProductUpdateRequestDto.ProductImageDto imageDto : imageDtos) {
            if (!imageDto.isDeleted()) {
                String fileName = imageDto.getId() != null ? 
                    productImageRepository.findById(imageDto.getId())
                        .map(ProductImage::getFileName)
                        .orElse(generateFileName(imageDto.getOriginalFileName())) :
                    generateFileName(imageDto.getOriginalFileName());
                
                ProductImage image = new ProductImage(
                    fileName,
                    imageDto.getOriginalFileName(),
                    imageDto.getImageUrl(),
                    imageDto.getFileSize(),
                    imageDto.getMimeType()
                );
                
                image.setPrimary(imageDto.isPrimary());
                image.setSortOrder(imageDto.getSortOrder());
                
                product.addImage(image);
            }
        }
    }
    
    /**
     * 파일명 생성
     * 
     * @param originalFileName 원본 파일명
     * @return 생성된 파일명
     */
    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
    
    /**
     * 상품 데이터 검증
     * 
     * @param request 검증할 상품 생성 요청 데이터
     */
    private void validateProductData(ProductCreateRequestDto request) {
        // 상품 갯수 검증
        if (request.getProductCount() == null || request.getProductCount() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_COUNT);
        }
        
        // 제조년도 검증
        if (request.getManufactureYear() != null) {
            int currentYear = java.time.Year.now().getValue();
            if (request.getManufactureYear() < 1800 || request.getManufactureYear() > currentYear + 10) {
                throw new BusinessException(ErrorCode.INVALID_MANUFACTURE_YEAR);
            }
        }
        
        // 재질 검증
        if (request.getMaterial() == null || request.getMaterial().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_MATERIAL);
        }
        
        // 사이즈 검증
        if (request.getSize() == null || request.getSize().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_SIZE);
        }
        
        // 브랜드 검증
        if (request.getBrand() == null || request.getBrand().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_BRAND);
        }
        
        // 상품 등급 검증
        if (request.getRank() == null) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_RANK);
        }
    }
    
    /**
     * 상품 업데이트 데이터 검증
     * 
     * @param request 검증할 상품 수정 요청 데이터
     */
    private void validateProductUpdateData(ProductUpdateRequestDto request) {
        // 상품 갯수 검증
        if (request.getProductCount() != null && request.getProductCount() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PRODUCT_COUNT);
        }
        
        // 제조년도 검증
        if (request.getManufactureYear() != null) {
            int currentYear = java.time.Year.now().getValue();
            if (request.getManufactureYear() < 1800 || request.getManufactureYear() > currentYear + 10) {
                throw new BusinessException(ErrorCode.INVALID_MANUFACTURE_YEAR);
            }
        }
        
        // 재질 검증
        if (request.getMaterial() != null && request.getMaterial().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_MATERIAL);
        }
        
        // 사이즈 검증
        if (request.getSize() != null && request.getSize().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_SIZE);
        }
        
        // 브랜드 검증
        if (request.getBrand() != null && request.getBrand().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_BRAND);
        }
    }
    
    /**
     * 상품 이미지 업로드
     * 
     * @param productId 상품 ID
     * @param files 업로드할 파일들
     * @param adminId 관리자 ID
     * @return 업로드된 이미지 URL 목록
     */
    @Transactional
    public List<String> uploadProductImages(Long productId, MultipartFile[] files, Long adminId) {
        log.info("상품 이미지 업로드: 상품={}, 파일개수={}, 관리자={}", productId, files.length, adminId);
        
        // 관리자 권한 검증
        validateProductManagePermission(adminId);
        
        // 상품 존재 확인
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        
        // 파일 검증
        validateImageFiles(files);
        
        List<String> imageUrls = new ArrayList<>();
        
        for (MultipartFile file : files) {
            try {
                // S3에 파일 업로드
                String imageUrl = s3Service.uploadProductImage(file, productId);
                
                // DB에 이미지 정보 저장
                ProductImage productImage = new ProductImage(
                    extractFileNameFromUrl(imageUrl),
                    file.getOriginalFilename(),
                    imageUrl,
                    file.getSize(),
                    file.getContentType()
                );
                
                // 첫 번째 이미지를 대표 이미지로 설정
                if (product.getImages().isEmpty()) {
                    productImage.setPrimary(true);
                }
                
                product.addImage(productImage);
                imageUrls.add(imageUrl);
                
            } catch (IOException e) {
                log.error("이미지 업로드 실패: 파일={}, 오류={}", file.getOriginalFilename(), e.getMessage());
                throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
        }
        
        productRepository.save(product);
        
        log.info("상품 이미지 업로드 완료: {} 개 파일", imageUrls.size());
        return imageUrls;
    }
    
    /**
     * 상품 이미지 삭제
     * 
     * @param productId 상품 ID
     * @param imageId 이미지 ID
     * @param adminId 관리자 ID
     */
    @Transactional
    public void deleteProductImage(Long productId, Long imageId, Long adminId) {
        log.info("상품 이미지 삭제: 상품={}, 이미지={}, 관리자={}", productId, imageId, adminId);
        
        // 관리자 권한 검증
        validateProductManagePermission(adminId);
        
        // 상품 존재 확인
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        
        // 이미지 존재 확인
        ProductImage productImage = productImageRepository.findById(imageId)
            .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
        
        // 이미지가 해당 상품에 속하는지 확인
        if (!productImage.getProduct().getId().equals(productId)) {
            throw new BusinessException(ErrorCode.IMAGE_NOT_BELONG_TO_PRODUCT);
        }
        
        // S3에서 파일 삭제
        s3Service.deleteImage(productImage.getImageUrl());
        
        // DB에서 이미지 삭제
        product.removeImage(productImage);
        productImageRepository.delete(productImage);
        
        log.info("상품 이미지 삭제 완료: 이미지ID={}", imageId);
    }
    
    /**
     * 이미지 파일 검증
     * 
     * @param files 검증할 파일들
     */
    private void validateImageFiles(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new BusinessException(ErrorCode.NO_IMAGE_FILES);
        }
        
        // 파일 개수 제한 (최대 10개)
        if (files.length > 10) {
            throw new BusinessException(ErrorCode.TOO_MANY_IMAGE_FILES);
        }
        
        for (MultipartFile file : files) {
            // 빈 파일 검증
            if (file.isEmpty()) {
                throw new BusinessException(ErrorCode.EMPTY_IMAGE_FILE);
            }
            
            // 파일 크기 검증 (최대 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new BusinessException(ErrorCode.IMAGE_FILE_TOO_LARGE);
            }
            
            // 파일 타입 검증
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new BusinessException(ErrorCode.INVALID_IMAGE_FILE_TYPE);
            }
            
            // 지원하는 이미지 형식 검증
            List<String> allowedTypes = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");
            if (!allowedTypes.contains(contentType.toLowerCase())) {
                throw new BusinessException(ErrorCode.UNSUPPORTED_IMAGE_FILE_TYPE);
            }
        }
    }
    
    /**
     * URL에서 파일명 추출
     * 
     * @param imageUrl 이미지 URL
     * @return 파일명
     */
    private String extractFileNameFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return UUID.randomUUID().toString();
        }
        
        String[] parts = imageUrl.split("/");
        return parts[parts.length - 1];
    }
}