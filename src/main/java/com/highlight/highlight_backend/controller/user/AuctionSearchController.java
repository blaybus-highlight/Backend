package com.highlight.highlight_backend.controller.user;

import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.dto.UserAuctionDetailResponseDto;
import com.highlight.highlight_backend.dto.UserAuctionResponseDto;
import com.highlight.highlight_backend.service.UserAuctionSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 경매 목록을 조회하고 세부사항을 확인하는 controller
 *
 * @Author 탁찬홍
 * @since 2025.08.15
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/products")
@Tag(name = "경매 목록 조회", description = "경매 목록 검색, 필터링, 상세조회 API (로그인 불필요)")
public class AuctionSearchController {

    private final UserAuctionSearchService userAuctionSearchService;

    /**
     *
     * @param minPrice -> 최소 가격
     * @param maxPrice -> 최대 가격
     * @param brand -> 브랜드 종류
     * @param eventName -> 이벤드 이름
     * @param category -> 필터링할 카테고리를 가져옵니다.
     * @param isPremium -> 프리미엄 상품 필터링 (true: 프리미엄만, false: 일반만, null: 전체)
     * @param status -> 경매 상태 필터링 (IN_PROGRESS: 진행중, SCHEDULED: 예정, ENDING_SOON: 마감임박)
     * @param sortCode -> ending, popular, newest (정렬 대상을 받아옵니다)
     */
    @GetMapping("/")
    @Operation(summary = "경매 목록 조회", description = "모든 경매 목록을 불러옵니다.")
    public ResponseEntity<ResponseDto<Page<UserAuctionResponseDto>>> home (
            // 1. 필터링 조건 (값이 안들어오면 null 저장)
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String eventName,
            @RequestParam(required = false) Boolean isPremium,
            @RequestParam(required = false) String status,
            // 2. 정렬 조건
            @RequestParam(defaultValue = "newest") String sortCode,
            Pageable pageable) {

        log.info("GET /api/public/products - 경매 목록 조회 요청 (상태: {}, 비로그인 사용자도 접근 가능)", status);

        Page<UserAuctionResponseDto> response = userAuctionSearchService.getProductsFiltered(
                category, minPrice, maxPrice, brand, eventName, isPremium, status, sortCode, pageable);

        return ResponseEntity.ok(
                ResponseDto.success(response, "경매 목록을 성공적으로 불러왔습니다."));
    }

    /**
     *
     * @param auctionId -> auction ID 를 받아서 조회
     * @return -> UserAuctionDetailResponseDto 를 반환
     */
    @GetMapping("/{auctionId}")
    @Operation(summary = "Auction 상세 정보 조회", description = "AuctionId 를 통해 상세 정보를 조회합니다.")
    public ResponseEntity<ResponseDto<UserAuctionDetailResponseDto>> getAuctionDetail(
            @PathVariable("auctionId") Long auctionId
    ) {
        log.info("GET /api/public/{} - 경매 목록 조회 요청 (비로그인 사용자도 접근 가능)", auctionId);

        UserAuctionDetailResponseDto response = userAuctionSearchService.getProductsDetail(auctionId);
        return ResponseEntity.ok(ResponseDto.success(response, "경매 상세 목록을 성공적으로 불러왔습니다"));
    }
}
