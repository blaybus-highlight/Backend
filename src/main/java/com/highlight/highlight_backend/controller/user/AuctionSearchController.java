package com.highlight.highlight_backend.controller.user;

import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.dto.UserAuctionResponseDto;
import com.highlight.highlight_backend.service.UserAuctionSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * +
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/products")
@Tag(name = "일반 유저 경매 조회 API", description = "일반 유저가 확인하는 API 입니다.")
public class AuctionSearchController {

    private final UserAuctionSearchService userAuctionSearchService;

    /**
     *
     * @param category
     * @param sortCode -> ending, popular, newest
     * @return
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
            // 2. 정렬 조건
            @RequestParam(defaultValue = "신규") String sortCode,
            Pageable pageable) {

        log.info("GET /api/user/products - 경매 목록 조회 요청 (비로그인 사용자도 접근 가능)");

        Page<UserAuctionResponseDto> response = userAuctionSearchService.getProductsFiltered(
                category, minPrice, maxPrice, brand, eventName, sortCode, pageable);

        return ResponseEntity.ok(
                ResponseDto.success(response, "경매 목록을 성공적으로 불러왔습니다."));
    }
}
