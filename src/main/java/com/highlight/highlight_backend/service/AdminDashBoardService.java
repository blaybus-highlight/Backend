package com.highlight.highlight_backend.service;

import com.highlight.highlight_backend.domain.Admin;
import com.highlight.highlight_backend.domain.Auction;
import com.highlight.highlight_backend.domain.ProductImage;
import com.highlight.highlight_backend.dto.dashboard.AdminDashBoardItemResponseDto;
import com.highlight.highlight_backend.dto.dashboard.AdminDashBoardStatsResponseDto;
import com.highlight.highlight_backend.exception.AdminErrorCode;
import com.highlight.highlight_backend.exception.BusinessException;
import com.highlight.highlight_backend.repository.AdminRepository;
import com.highlight.highlight_backend.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashBoardService {

    private final AdminRepository adminRepository;
    private final AuctionRepository auctionRepository;

    public AdminDashBoardStatsResponseDto getDashboardStats(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(AdminErrorCode.ADMIN_NOT_FOUND));

        AdminDashBoardStatsResponseDto responseDto = new AdminDashBoardStatsResponseDto();
        responseDto.setInProgress(admin.getInProgress());
        responseDto.setCompleted(admin.getCompleted());
        responseDto.setPending(admin.getPending());

        return responseDto;
    }

    public List<AdminDashBoardItemResponseDto> getDashboardItems() {
        List<Auction> inProgressAuctions = auctionRepository.findByStatus(Auction.AuctionStatus.IN_PROGRESS, PageRequest.of(0, 5)).getContent();

        return inProgressAuctions.stream()
                .map(auction -> {
                    AdminDashBoardItemResponseDto dto = new AdminDashBoardItemResponseDto();
                    dto.setAuctionId(auction.getId());
                    dto.setProductName(auction.getProduct().getProductName());
                    dto.setCurrentBid(auction.getCurrentHighestBid());

                    ProductImage primaryImage = auction.getProduct().getPrimaryImage();
                    if (primaryImage != null) {
                        dto.setProductImageUrl(primaryImage.getImageUrl());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
}
