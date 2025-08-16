package com.highlight.highlight_backend.controller.user;

import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.dto.UserAuctionDetailResponseDto;
import com.highlight.highlight_backend.dto.UserAuctionResponseDto;
import com.highlight.highlight_backend.service.UserAuctionSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
        summary = "경매 목록 조회 및 검색", 
        description = "모든 경매 목록을 필터링과 정렬 조건에 따라 조회합니다. 로그인 없이 접근 가능한 공개 API입니다. 카테고리, 가격 범위, 브랜드, 경매 상태 등으로 필터링할 수 있으며, 다양한 정렬 옵션을 제공합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "경매 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ResponseDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 검색 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ResponseDto<Page<UserAuctionResponseDto>>> home (
            @Parameter(description = "카테고리 필터 (PROPS, FURNITURE, HOME_APPLIANCES, SCULPTURE, FASHION, CERAMICS, PAINTING)", example = "FURNITURE")
            @RequestParam(required = false) String category,
            @Parameter(description = "최소 가격 (원)", example = "10000")
            @RequestParam(required = false) Long minPrice,
            @Parameter(description = "최대 가격 (원)", example = "100000")
            @RequestParam(required = false) Long maxPrice,
            @Parameter(description = "브랜드명", example = "삼성")
            @RequestParam(required = false) String brand,
            @Parameter(description = "이벤트명", example = "신년 이벤트")
            @RequestParam(required = false) String eventName,
            @Parameter(description = "프리미엄 상품 필터 (true: 프리미엄만, false: 일반만, null: 전체)", example = "true")
            @RequestParam(required = false) Boolean isPremium,
            @Parameter(description = "경매 상태 (IN_PROGRESS: 진행중, SCHEDULED: 예정, ENDING_SOON: 마감임박)", example = "IN_PROGRESS")
            @RequestParam(required = false) String status,
            @Parameter(description = "정렬 기준 (ending: 마감임박순, popular: 인기순, newest: 최신순)", example = "newest")
            @RequestParam(defaultValue = "newest") String sortCode,
            @Parameter(description = "페이징 정보 (page, size, sort)")
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
    @Operation(
        summary = "경매 상세 정보 조회", 
        description = "특정 경매의 상세 정보를 조회합니다. 상품 정보, 현재 입찰 현황, 판매자 정보, 이미지 등 모든 상세 정보를 포함합니다. 로그인 없이 접근 가능합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "경매 상세 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = ResponseDto.class))
        ),
        @ApiResponse(responseCode = "404", description = "경매를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ResponseDto<UserAuctionDetailResponseDto>> getAuctionDetail(
            @Parameter(description = "조회할 경매의 고유 ID", required = true, example = "1")
            @PathVariable("auctionId") Long auctionId
    ) {
        log.info("GET /api/public/{} - 경매 목록 조회 요청 (비로그인 사용자도 접근 가능)", auctionId);

        UserAuctionDetailResponseDto response = userAuctionSearchService.getProductsDetail(auctionId);
        return ResponseEntity.ok(ResponseDto.success(response, "경매 상세 목록을 성공적으로 불러왔습니다"));
    }
}
