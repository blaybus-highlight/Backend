package com.highlight.highlight_backend.dto.dashboard;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdminDashBoardItemResponseDto {

    private Long auctionId;

    private String productName;

    private BigDecimal currentBid;

    private String productImageUrl;
}
