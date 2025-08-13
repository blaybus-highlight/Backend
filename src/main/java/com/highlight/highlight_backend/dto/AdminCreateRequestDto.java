package com.highlight.highlight_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자 계정 생성 요청 DTO
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Getter
@NoArgsConstructor
public class AdminCreateRequestDto {
    
    /**
     * 관리자 ID (로그인용)
     */
    private String adminId;
    
    /**
     * 관리자 비밀번호
     */
    private String password;
    
    /**
     * 관리자 이름
     */
    private String adminName;
    
    /**
     * 관리자 이메일
     */
    private String email;
    
    /**
     * 상품 등록 및 수정 권한
     */
    private boolean canManageProducts;
    
    /**
     * 경매 설정 권한
     */
    private boolean canManageAuctions;
    
    /**
     * 결제 상태 확인 권한
     */
    private boolean canManagePayments;
    
    /**
     * 배송 관리 권한
     */
    private boolean canManageShipping;
    
    /**
     * 낙찰/유찰 관리 권한
     */
    private boolean canManageAuctionResults;
    
    /**
     * 문의 답변 권한
     */
    private boolean canManageInquiries;
}