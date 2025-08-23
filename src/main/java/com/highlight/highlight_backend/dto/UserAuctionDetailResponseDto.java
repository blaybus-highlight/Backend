package com.highlight.highlight_backend.dto;

import com.highlight.highlight_backend.domain.Auction;
import com.highlight.highlight_backend.domain.Product;
import com.highlight.highlight_backend.domain.Seller;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 일반 User 에게 상품 Detail 페이지에 보낼 정보
 *
 * @author 탁찬홍
 * @since 2025.08.15
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserAuctionDetailResponseDto {

    private Long auctionId;
    private String productName;
    private String shortDescription;  // 짧은 소개
    private String brand;
    private String history;  // 히스토리
    private String expectedEffects;  // 기대효과
    private String detailedInfo;  // 상세정보
    private List<ProductResponseDto.ProductImageResponseDto> images;  // 이미지
    private String status;  // 상품상태
    private String rank;
    private String category;
    private String material;
    private String size;
    private Integer manufactureYear;
    private Boolean isPremium;
    private String condition;

    private String sellerName;
    private String sellerDescription;
    private String sellerProfileImageUrl;
    private String sellerPhoneNumber;
    private String sellerEmail;
    private String sellerAddress;
    private BigDecimal sellerRating;

    private LocalDateTime scheduledStartTime;  // 경매 시작 시간
    private LocalDateTime scheduledEndTime;  // 경매 종료 시간

    private BigDecimal currentHighestBid;  // 현재가
    private BigDecimal buyItNowPrice;  // 즉시구매가
    private BigDecimal maxBid;
    private BigDecimal minimumBid;
    
    private BigDecimal point;  // 적립 될 포인트
    private Long productCount;


    public static UserAuctionDetailResponseDto from(Auction auction) {
        Product product = auction.getProduct();
        Seller seller = product.getSeller();
        List<ProductResponseDto.ProductImageResponseDto> imageDtos = product.getImages().stream()
                .map(ProductResponseDto.ProductImageResponseDto::from)
                .collect(Collectors.toList());

        UserAuctionDetailResponseDtoBuilder builder = UserAuctionDetailResponseDto.builder()
                .auctionId(auction.getId())
                .productName(product.getProductName())
                .shortDescription(product.getShortDescription())
                .history(product.getHistory())
                .expectedEffects(product.getExpectedEffects())
                .detailedInfo(product.getDetailedInfo())
                .images(imageDtos)
                .status(product.getStatus().getDescription())
                .rank(product.getRank().getDescription())
                .category(product.getCategory().getDisplayName())
                .material(product.getMaterial())
                .size(product.getSize())
                .manufactureYear(product.getManufactureYear())
                .isPremium(product.getIsPremium())
                .scheduledStartTime(auction.getScheduledStartTime())
                .scheduledEndTime(auction.getScheduledEndTime())
                .currentHighestBid(auction.getCurrentHighestBid())
                .buyItNowPrice(auction.getBuyItNowPrice())
                .maxBid(auction.getMaxBid())
                .minimumBid(auction.getMinimumBid())
                .condition(product.getCondition())
                .productCount(product.getProductCount())
                .brand(product.getBrand());

        if (seller != null) {
            builder.sellerName(seller.getSellerName())
                   .sellerDescription(seller.getDescription())
                   .sellerProfileImageUrl(seller.getProfileImageUrl())
                   .sellerPhoneNumber(seller.getPhoneNumber())
                   .sellerEmail(seller.getEmail())
                   .sellerAddress(seller.getAddress())
                   .sellerRating(seller.getRating());
        }

        return builder.build();
    }
}