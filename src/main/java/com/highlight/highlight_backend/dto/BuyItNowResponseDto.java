package com.highlight.highlight_backend.dto;

import com.highlight.highlight_backend.domain.Auction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 즉시구매 응답 DTO
 * 
 * 즉시구매 완료 후 반환되는 응답 데이터입니다.
 * 
 * @author 전우선
 * @since 2025.08.16
 */
@Getter
@Builder
@Schema(description = "즉시구매 응답 DTO")
public class BuyItNowResponseDto {
    
    /**
     * 경매 ID
     */
    @Schema(description = "경매 ID", example = "1")
    private Long auctionId;
    
    /**
     * 상품 ID
     */
    @Schema(description = "상품 ID", example = "1")
    private Long productId;
    
    /**
     * 상품명
     */
    @Schema(description = "상품명", example = "빈티지 의자")
    private String productName;
    
    /**
     * 즉시구매가
     */
    @Schema(description = "즉시구매가", example = "100000")
    private BigDecimal buyItNowPrice;
    
    /**
     * 구매자 ID
     */
    @Schema(description = "구매자 ID", example = "1")
    private Long buyerId;
    
    /**
     * 즉시구매 완료 시간
     */
    @Schema(description = "즉시구매 완료 시간")
    private LocalDateTime purchaseCompletedAt;
    
    /**
     * 경매 상태
     */
    @Schema(description = "경매 상태", example = "COMPLETED")
    private String auctionStatus;
    
    /**
     * 결제 상태 (추후 결제 시스템 연동 시 사용)
     */
    @Schema(description = "결제 상태", example = "COMPLETED")
    private String paymentStatus;
    
    /**
     * 경매 정보로부터 BuyItNowResponseDto 생성
     */
    public static BuyItNowResponseDto from(Auction auction, Long buyerId) {
        return BuyItNowResponseDto.builder()
                .auctionId(auction.getId())
                .productId(auction.getProduct().getId())
                .productName(auction.getProduct().getProductName())
                .buyItNowPrice(auction.getBuyItNowPrice())
                .buyerId(buyerId)
                .purchaseCompletedAt(auction.getActualEndTime())
                .auctionStatus(auction.getStatus().name())
                .paymentStatus("COMPLETED") // 임시로 완료 상태
                .build();
    }
}