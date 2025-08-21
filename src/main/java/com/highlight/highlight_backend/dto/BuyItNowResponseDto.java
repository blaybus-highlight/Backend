package com.highlight.highlight_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 즉시 구매 응답 DTO
 * 
 * @author 탁찬홍
 * @since 2025.08.21
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "즉시 구매 응답 DTO")
public class BuyItNowResponseDto {
    
    /**
     * 경매 ID
     */
    @Schema(description = "경매 ID", example = "1")
    private Long auctionId;
    
    /**
     * 상품명
     */
    @Schema(description = "상품명", example = "아이폰 14 프로")
    private String productName;
    
    /**
     * 즉시 구매가
     */
    @Schema(description = "즉시 구매가", example = "100000")
    private BigDecimal buyItNowPrice;
    
    /**
     * 사용한 포인트
     */
    @Schema(description = "사용한 포인트", example = "10000")
    private BigDecimal usedPointAmount;
    
    /**
     * 실제 결제 금액
     */
    @Schema(description = "실제 결제 금액", example = "90000")
    private BigDecimal actualPaymentAmount;
    
    /**
     * 포인트 적립 금액
     */
    @Schema(description = "결제 금액의 1% 포인트 적립", example = "900")
    private BigDecimal pointReward;
    
    /**
     * 결제 후 남은 포인트
     */
    @Schema(description = "결제 후 남은 포인트", example = "5900")
    private BigDecimal remainingPoint;
    
    /**
     * 구매 완료 시간
     */
    @Schema(description = "구매 완료 시간", example = "2025-08-19T16:30:00")
    private LocalDateTime completedAt;
}