package com.highlight.highlight_backend.dto;

import com.highlight.highlight_backend.domain.Auction;
import com.highlight.highlight_backend.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 일반 User 에게 상품 Detail 페이지에 보낼 정보
 */
@Getter
@AllArgsConstructor
@Builder
public class UserAuctionDetailResponseDto {

    //private String source; // 출처
    private Long auctionId;
    private String shortDescription;  // 짧은 소개
    private String history;  // 히스토리
    private String basicInfo;  // 기본정보
    private String expectedEffects;  // 기대효과
    private String detailedInfo;  // 상세정보
    private List<ProductResponseDto.ProductImageResponseDto> images;  // 이미지
    private String status;  // 상품상태
    //private String size; // 상품 사이즈

    private LocalDateTime scheduledStartTime;  // 경매 시작 시간
    private LocalDateTime scheduledEndTime;  // 경매 종료 시간

    private BigDecimal entranceFee;  // 입장료
    private BigDecimal currentHighestBid;  // 현재가
    private BigDecimal buyItNowPrice;  // 즉시구매가


    public static UserAuctionDetailResponseDto from(Auction auction) {
        Product product = auction.getProduct();
        List<ProductResponseDto.ProductImageResponseDto> imageDtos = product.getImages().stream()
                .map(ProductResponseDto.ProductImageResponseDto::from)
                .collect(Collectors.toList());

        return UserAuctionDetailResponseDto.builder()
                .auctionId(auction.getId())
                .shortDescription(product.getShortDescription())
                .history(product.getHistory())
                .basicInfo(product.getBasicInfo())
                .expectedEffects(product.getExpectedEffects())
                .detailedInfo(product.getDetailedInfo())
                .images(imageDtos)
                .status(product.getStatus().getDescription())
                .scheduledStartTime(auction.getScheduledStartTime())
                .scheduledEndTime(auction.getScheduledEndTime())
                .entranceFee(product.getEntranceFee())
                .currentHighestBid(auction.getCurrentHighestBid())
                .buyItNowPrice(auction.getBuyItNowPrice())
                .build();

    }
}
