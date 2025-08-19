package com.highlight.highlight_backend.dto;

import com.highlight.highlight_backend.domain.Auction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 경매 응답 DTO
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Getter
@AllArgsConstructor
public class AuctionResponseDto {
    
    /**
     * 경매 ID
     */
    private Long id;
    
    /**
     * 경매 상품 정보
     */
    private ProductResponseDto product;
    
    /**
     * 경매 상태
     */
    private Auction.AuctionStatus status;
    
    /**
     * 경매 상태 설명
     */
    private String statusDescription;
    
    /**
     * 경매 시작 예정 시간
     */
    private LocalDateTime scheduledStartTime;
    
    /**
     * 경매 종료 예정 시간
     */
    private LocalDateTime scheduledEndTime;
    
    /**
     * 실제 경매 시작 시간
     */
    private LocalDateTime actualStartTime;
    
    /**
     * 실제 경매 종료 시간
     */
    private LocalDateTime actualEndTime;

    /**
     * 시작가
     */
    private BigDecimal startPrice;
    /**
     * 현재 최고 입찰가
     */
    private BigDecimal currentHighestBid;
    
    /**
     * 즉시구매가
     */
    private BigDecimal buyItNowPrice;
    
    /**
     * 총 입찰 참여자 수
     */
    private Integer totalBidders;
    
    /**
     * 총 입찰 횟수
     */
    private Integer totalBids;
    
    /**
     * 경매 생성한 관리자 ID
     */
    private Long createdBy;
    
    /**
     * 경매 시작한 관리자 ID
     */
    private Long startedBy;
    
    /**
     * 경매 종료한 관리자 ID
     */
    private Long endedBy;
    
    /**
     * 종료 사유
     */
    private String endReason;
    
    /**
     * 경매 설명/메모
     */
    private String description;
    
    /**
     * 생성 시간
     */
    private LocalDateTime createdAt;
    
    /**
     * 수정 시간
     */
    private LocalDateTime updatedAt;
    
    /**
     * Auction 엔티티로부터 DTO 생성
     */
    public static AuctionResponseDto from(Auction auction) {
        ProductResponseDto productDto = auction.getProduct() != null ? 
            ProductResponseDto.from(auction.getProduct()) : null;
        
        return new AuctionResponseDto(
            auction.getId(),
            productDto,
            auction.getStatus(),
            auction.getStatus().getDescription(),
            auction.getScheduledStartTime(),
            auction.getScheduledEndTime(),
            auction.getActualStartTime(),
            auction.getActualEndTime(),
            auction.getCurrentHighestBid(),
            auction.getBuyItNowPrice(),
            auction.getStartPrice(),
            auction.getTotalBidders(),
            auction.getTotalBids(),
            auction.getCreatedBy(),
            auction.getStartedBy(),
            auction.getEndedBy(),
            auction.getEndReason(),
            auction.getDescription(),
            auction.getCreatedAt(),
            auction.getUpdatedAt()
        );
    }
    
    /**
     * 상품 정보 없이 경매 기본 정보만 포함한 DTO 생성
     */
    public static AuctionResponseDto fromWithoutProduct(Auction auction) {
        return new AuctionResponseDto(
            auction.getId(),
            null, // 상품 정보 제외
            auction.getStatus(),
            auction.getStatus().getDescription(),
            auction.getScheduledStartTime(),
            auction.getScheduledEndTime(),
            auction.getActualStartTime(),
            auction.getActualEndTime(),
            auction.getCurrentHighestBid(),
            auction.getBuyItNowPrice(),
            auction.getStartPrice(),
            auction.getTotalBidders(),
            auction.getTotalBids(),
            auction.getCreatedBy(),
            auction.getStartedBy(),
            auction.getEndedBy(),
            auction.getEndReason(),
            auction.getDescription(),
            auction.getCreatedAt(),
            auction.getUpdatedAt()
        );
    }
}