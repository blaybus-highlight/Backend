package com.highlight.highlight_backend.dto;

import com.highlight.highlight_backend.domain.Bid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 입찰 응답 DTO
 * 
 * @author 전우선
 * @since 2025.08.15
 */
@Getter
@AllArgsConstructor
@Schema(description = "입찰 응답 DTO")
public class BidResponseDto {
    
    /**
     * 입찰 ID
     */
    @Schema(description = "입찰 ID", example = "1")
    private Long bidId;
    
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
     * 사용자 닉네임 (마스킹 처리)
     */
    @Schema(description = "사용자 닉네임 (마스킹 처리)", example = "홍**")
    private String bidderNickname;
    
    /**
     * 입찰 금액
     */
    @Schema(description = "입찰 금액", example = "50000")
    private BigDecimal bidAmount;
    
    /**
     * 입찰 상태
     */
    @Schema(description = "입찰 상태", example = "WINNING")
    private String status;
    
    /**
     * 입찰 상태 설명
     */
    @Schema(description = "입찰 상태 설명", example = "최고 입찰 중")
    private String statusDescription;
    
    /**
     * 자동 입찰 여부
     */
    @Schema(description = "자동 입찰 여부", example = "false")
    private Boolean isAutoBid;
    
    /**
     * 입찰 시간
     */
    @Schema(description = "입찰 시간", example = "2025-08-15T14:30:00")
    private LocalDateTime bidTime;
    
    /**
     * 현재 최고 입찰인지 여부
     */
    @Schema(description = "현재 최고 입찰인지 여부", example = "true")
    private Boolean isWinning;
    
    /**
     * Bid 엔티티로부터 DTO 생성
     */
    public static BidResponseDto from(Bid bid) {
        String maskedNickname = maskNickname(bid.getUser().getNickname());
        boolean isWinning = bid.getStatus() == Bid.BidStatus.WINNING;
        
        return new BidResponseDto(
            bid.getId(),
            bid.getAuction().getId(),
            bid.getAuction().getProduct().getProductName(),
            maskedNickname,
            bid.getBidAmount(),
            bid.getStatus().name(),
            bid.getStatus().getDescription(),
            bid.isAutoBid(),
            bid.getCreatedAt(),
            isWinning
        );
    }
    
    /**
     * 내 입찰 내역용 DTO 생성 (닉네임 마스킹 없음)
     */
    public static BidResponseDto fromMyBid(Bid bid) {
        boolean isWinning = bid.getStatus() == Bid.BidStatus.WINNING;
        
        return new BidResponseDto(
            bid.getId(),
            bid.getAuction().getId(),
            bid.getAuction().getProduct().getProductName(),
            bid.getUser().getNickname(),
            bid.getBidAmount(),
            bid.getStatus().name(),
            bid.getStatus().getDescription(),
            bid.isAutoBid(),
            bid.getCreatedAt(),
            isWinning
        );
    }
    
    /**
     * 닉네임 마스킹 처리
     * 예: "홍길동" -> "홍**"
     */
    private static String maskNickname(String nickname) {
        if (nickname == null || nickname.length() <= 1) {
            return nickname;
        }
        
        if (nickname.length() == 2) {
            return nickname.charAt(0) + "*";
        }
        
        // 3글자 이상인 경우: 첫 글자 + ** + 마지막 글자
        if (nickname.length() >= 3) {
            return nickname.charAt(0) + "**";
        }
        
        return nickname;
    }
}