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
@RequestMapping("/api/products")
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
}