package com.highlight.highlight_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자 계정 수정 요청 DTO
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Getter
@NoArgsConstructor
public class AdminUpdateRequestDto {
    
    /**
     * 관리자 이름
     */
    private String adminName;
    
    /**
     * 관리자 이메일
     */
    private String email;
    
    /**
     * 계정 활성화 상태
     */
    private Boolean isActive;
    
    /**
     * 상품 등록 및 수정 권한
     */
    private Boolean canManageProducts;
    
    /**
     * 경매 설정 권한
     */
    private Boolean canManageAuctions;
    
    /**
     * 결제 상태 확인 권한
     */
    private Boolean canManagePayments;
    
    /**
     * 배송 관리 권한
     */
    private Boolean canManageShipping;
    
    /**
     * 낙찰/유찰 관리 권한
     */
    private Boolean canManageAuctionResults;
    
    /**
     * 문의 답변 권한
     */
    private Boolean canManageInquiries;
}