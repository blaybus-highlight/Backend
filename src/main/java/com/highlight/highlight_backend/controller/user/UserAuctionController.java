package com.highlight.highlight_backend.controller.user;

import com.highlight.highlight_backend.dto.BuyItNowRequestDto;
import com.highlight.highlight_backend.dto.BuyItNowResponseDto;
import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.service.AuctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 경매 참여 컨트롤러
 * 
 * 사용자가 경매에 참여하는 기능(입찰, 즉시구매 등)을 제공합니다.
 * 
 * @author 전우선
 * @since 2025.08.16
 */
@Slf4j
@RestController
@RequestMapping("/api/user/auctions")
@RequiredArgsConstructor
@Tag(name = "사용자 경매 참여 API", description = "입찰, 즉시구매 등 경매 참여 관련 API")
public class UserAuctionController {
    
    private final AuctionService auctionService;
    
    /**
     * 즉시구매
     * 
     * @param auctionId 즉시구매할 경매 ID
     * @param request 즉시구매 요청 데이터
     * @param authentication 현재 로그인한 사용자 정보
     * @return 즉시구매 완료 정보
     */
    @PostMapping("/{auctionId}/buy-it-now")
    @Operation(summary = "즉시구매", 
               description = "설정된 즉시구매가로 상품을 즉시 구매합니다. 재고 1개 상품만 가능합니다.")
    public ResponseEntity<ResponseDto<BuyItNowResponseDto>> buyItNow(
            @PathVariable Long auctionId,
            @Valid @RequestBody BuyItNowRequestDto request,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        log.info("POST /api/user/auctions/{}/buy-it-now - 즉시구매 요청 (사용자: {})", 
                auctionId, userId);
        
        BuyItNowResponseDto response = auctionService.buyItNow(auctionId, request, userId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "즉시구매가 완료되었습니다.")
        );
    }
}