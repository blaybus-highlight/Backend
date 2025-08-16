package com.highlight.highlight_backend.controller;

import com.highlight.highlight_backend.dto.ProductCreateRequestDto;
import com.highlight.highlight_backend.dto.ProductResponseDto;
import com.highlight.highlight_backend.dto.ProductUpdateRequestDto;
import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 상품 관리 컨트롤러
 * 
 * 경매 진행 상품의 등록, 수정, 조회, 삭제 API를 제공합니다.
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Tag(name = "상품 관리 API", description = "경매 상품 등록/수정/조회/삭제 관련 API")
public class ProductController {
    
    private final ProductService productService;
    
    /**
     * 상품 등록
     * 
     * @param request 상품 등록 요청 데이터
     * @param authentication 현재 로그인한 관리자 정보
     * @return 등록된 상품 정보
     */
    @PostMapping
    @Operation(summary = "상품 등록", 
               description = "새로운 경매 상품을 등록합니다. 상품 사진, 상품명, 상품소개(25자), 히스토리, 기본 정보, 기대효과, 상세 정보, 시작가, 입장료 등을 입력할 수 있습니다.")
    public ResponseEntity<ResponseDto<ProductResponseDto>> createProduct(
            @Valid @RequestBody ProductCreateRequestDto request,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("POST /api/products - 상품 등록 요청: {} (관리자: {})", 
                request.getProductName(), adminId);
        
        ProductResponseDto response = productService.createProduct(request, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "상품이 성공적으로 등록되었습니다.")
        );
    }
    
    /**
     * 상품 목록 조회
     * 
     * @param pageable 페이징 정보
     * @param authentication 현재 로그인한 관리자 정보
     * @return 상품 목록
     */
    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "등록된 상품 목록을 페이징하여 조회합니다.")
    public ResponseEntity<ResponseDto<Page<ProductResponseDto>>> getProductList(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("GET /api/products - 상품 목록 조회 요청 (관리자: {})", adminId);
        
        Page<ProductResponseDto> response = productService.getProductList(pageable, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "상품 목록을 성공적으로 조회했습니다.")
        );
    }
    
    /**
     * 상품 상세 조회
     * 
     * @param productId 조회할 상품 ID
     * @param authentication 현재 로그인한 관리자 정보
     * @return 상품 상세 정보
     */
    @GetMapping("/{productId}")
    @Operation(summary = "상품 상세 조회", 
               description = "특정 상품의 상세 정보를 조회합니다. 수정 시 기존 내용을 불러오는데 사용할 수 있습니다.")
    public ResponseEntity<ResponseDto<ProductResponseDto>> getProduct(
            @PathVariable Long productId,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("GET /api/products/{} - 상품 상세 조회 요청 (관리자: {})", 
                productId, adminId);
        
        ProductResponseDto response = productService.getProduct(productId, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "상품 정보를 성공적으로 조회했습니다.")
        );
    }
    
    /**
     * 상품 수정
     * 
     * @param productId 수정할 상품 ID
     * @param request 상품 수정 요청 데이터
     * @param authentication 현재 로그인한 관리자 정보
     * @return 수정된 상품 정보
     */
    @PutMapping("/{productId}")
    @Operation(summary = "상품 수정", 
               description = "기존 상품의 정보를 수정합니다. 상품 사진, 상품명, 상품소개, 히스토리, 기본 정보, 기대효과, 상세 정보, 시작가, 입장료 등을 수정할 수 있습니다.")
    public ResponseEntity<ResponseDto<ProductResponseDto>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequestDto request,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("PUT /api/products/{} - 상품 수정 요청 (관리자: {})", 
                productId, adminId);
        
        ProductResponseDto response = productService.updateProduct(productId, request, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "상품이 성공적으로 수정되었습니다.")
        );
    }
    
    /**
     * 상품 삭제
     * 
     * @param productId 삭제할 상품 ID
     * @param authentication 현재 로그인한 관리자 정보
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다. 경매 중인 상품은 삭제할 수 없습니다.")
    public ResponseEntity<ResponseDto<String>> deleteProduct(
            @PathVariable Long productId,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("DELETE /api/products/{} - 상품 삭제 요청 (관리자: {})", 
                productId, adminId);
        
        productService.deleteProduct(productId, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success("SUCCESS", "상품이 성공적으로 삭제되었습니다.")
        );
    }

    /**
     * 관련 상품 추천 조회 (공개 API)
     * 
     * @param productId 기준 상품 ID
     * @param size 추천 상품 개수 (기본 4개)
     * @return 추천 상품 목록
     */
    @GetMapping("/{productId}/recommendations")
    @Operation(summary = "관련 상품 추천", description = "특정 상품과 관련된 추천 상품 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<Page<ProductResponseDto>>> getRecommendedProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "4") int size) {
        
        log.info("GET /api/products/{}/recommendations - 관련 상품 추천 조회", productId);
        
        Page<ProductResponseDto> response = productService.getRecommendedProducts(productId, size);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "관련 상품을 성공적으로 조회했습니다.")
        );
    }
    
    /**
     * 상품 프리미엄 설정 변경
     * 
     * @param productId 상품 ID
     * @param isPremium 프리미엄 설정 여부
     * @param authentication 현재 로그인한 관리자 정보
     * @return 업데이트된 상품 정보
     */
    @PatchMapping("/{productId}/premium")
    @Operation(summary = "상품 프리미엄 설정", description = "관리자가 상품의 프리미엄 여부를 설정합니다.")
    public ResponseEntity<ResponseDto<ProductResponseDto>> updateProductPremium(
            @PathVariable Long productId,
            @RequestParam Boolean isPremium,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("PATCH /api/admin/products/{}/premium - 상품 프리미엄 설정 (관리자: {}, 프리미엄: {})", 
                productId, adminId, isPremium);
        
        ProductResponseDto response = productService.updateProductPremium(productId, isPremium, adminId);
        
        String message = isPremium ? "상품이 프리미엄으로 설정되었습니다." : "상품의 프리미엄 설정이 해제되었습니다.";
        
        return ResponseEntity.ok(
            ResponseDto.success(response, message)
        );
    }
    
    /**
     * 상품 이미지 업로드
     * 
     * @param productId 상품 ID
     * @param files 업로드할 이미지 파일들
     * @param authentication 현재 로그인한 관리자 정보
     * @return 업로드된 이미지 URL 목록
     */
    @PostMapping("/{productId}/images")
    @Operation(summary = "상품 이미지 업로드", 
               description = "상품에 이미지를 업로드합니다. 여러 개의 이미지를 한 번에 업로드할 수 있습니다.")
    public ResponseEntity<ResponseDto<java.util.List<String>>> uploadProductImages(
            @PathVariable Long productId,
            @RequestParam("files") MultipartFile[] files,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("POST /api/admin/products/{}/images - 상품 이미지 업로드 요청: {} 개 파일 (관리자: {})", 
                productId, files.length, adminId);
        
        java.util.List<String> imageUrls = productService.uploadProductImages(productId, files, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(imageUrls, "이미지가 성공적으로 업로드되었습니다.")
        );
    }
    
    /**
     * 상품 이미지 삭제
     * 
     * @param productId 상품 ID
     * @param imageId 삭제할 이미지 ID
     * @param authentication 현재 로그인한 관리자 정보
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/{productId}/images/{imageId}")
    @Operation(summary = "상품 이미지 삭제", description = "상품의 특정 이미지를 삭제합니다.")
    public ResponseEntity<ResponseDto<String>> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("DELETE /api/admin/products/{}/images/{} - 상품 이미지 삭제 요청 (관리자: {})", 
                productId, imageId, adminId);
        
        productService.deleteProductImage(productId, imageId, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success("SUCCESS", "이미지가 성공적으로 삭제되었습니다.")
        );
    }
}