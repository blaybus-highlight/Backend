package com.highlight.highlight_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 경매 예약 요청 DTO
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Getter
@NoArgsConstructor
public class AuctionScheduleRequestDto {
    
    /**
     * 경매할 상품 ID
     */
    private Long productId;
    
    /**
     * 경매 시작 예정 시간
     */
    private LocalDateTime scheduledStartTime;
    
    /**
     * 경매 종료 예정 시간
     */
    private LocalDateTime scheduledEndTime;
    
    /**
     * 경매 설명/메모
     */
    private String description;
}