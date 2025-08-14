package com.highlight.highlight_backend.controller;

import com.highlight.highlight_backend.dto.*;
import com.highlight.highlight_backend.service.AuctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 경매 관리 컨트롤러
 * 
 * 경매 예약, 시작, 종료, 중단 API를 제공합니다.
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/auctions")
@RequiredArgsConstructor
@Tag(name = "경매 관리 API", description = "경매 예약/시작/종료/중단 관련 API")
public class AuctionController {
    
    private final AuctionService auctionService;
    
    /**
     * 경매 예약
     * 
     * @param request 경매 예약 요청 데이터
     * @param authentication 현재 로그인한 관리자 정보
     * @return 예약된 경매 정보
     */
    @PostMapping("/schedule")
    @Operation(summary = "경매 예약", 
               description = "상품을 경매에 예약합니다. 경매 시작/종료 시간을 직접 입력하여 설정할 수 있습니다.")
    public ResponseEntity<ResponseDto<AuctionResponseDto>> scheduleAuction(
            @Valid @RequestBody AuctionScheduleRequestDto request,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("POST /api/auctions/schedule - 경매 예약 요청: 상품 {} (관리자: {})", 
                request.getProductId(), adminId);
        
        AuctionResponseDto response = auctionService.scheduleAuction(request, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "경매가 성공적으로 예약되었습니다.")
        );
    }
    
    /**
     * 경매 시작
     * 
     * @param auctionId 시작할 경매 ID
     * @param request 경매 시작 요청 데이터
     * @param authentication 현재 로그인한 관리자 정보
     * @return 시작된 경매 정보
     */
    @PostMapping("/{auctionId}/start")
    @Operation(summary = "경매 시작", 
               description = "예약된 경매를 시작합니다. 즉시 시작하거나 시간을 직접 입력하여 시작할 수 있습니다.")
    public ResponseEntity<ResponseDto<AuctionResponseDto>> startAuction(
            @PathVariable Long auctionId,
            @Valid @RequestBody AuctionStartRequestDto request,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("POST /api/auctions/{}/start - 경매 시작 요청 (관리자: {}, 즉시시작: {})", 
                auctionId, adminId, request.isImmediateStart());
        
        AuctionResponseDto response = auctionService.startAuction(auctionId, request, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "경매가 성공적으로 시작되었습니다.")
        );
    }
    
    /**
     * 경매 즉시 시작 (간편 API)
     * 
     * @param auctionId 시작할 경매 ID
     * @param authentication 현재 로그인한 관리자 정보
     * @return 시작된 경매 정보
     */
    @PostMapping("/{auctionId}/start-now")
    @Operation(summary = "경매 즉시 시작", 
               description = "버튼 클릭으로 경매를 즉시 시작합니다. 현재 시간부터 시작되며 기존 종료 시간을 유지합니다.")
    public ResponseEntity<ResponseDto<AuctionResponseDto>> startAuctionNow(
            @PathVariable Long auctionId,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("POST /api/auctions/{}/start-now - 경매 즉시 시작 요청 (관리자: {})", 
                auctionId, adminId);
        
        // 즉시 시작 요청 생성
        AuctionStartRequestDto request = new AuctionStartRequestDto();
        // immediateStart는 기본값이 false이므로 별도 설정 필요
        AuctionResponseDto response = auctionService.startAuction(auctionId, 
            new AuctionStartRequestDto() {{ setImmediateStart(true); }}, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "경매가 즉시 시작되었습니다.")
        );
    }
    
    /**
     * 경매 종료
     * 
     * @param auctionId 종료할 경매 ID
     * @param request 경매 종료 요청 데이터
     * @param authentication 현재 로그인한 관리자 정보
     * @return 종료된 경매 정보
     */
    @PostMapping("/{auctionId}/end")
    @Operation(summary = "경매 종료", 
               description = "진행 중인 경매를 종료합니다. 정상 종료 또는 중단으로 처리할 수 있습니다.")
    public ResponseEntity<ResponseDto<AuctionResponseDto>> endAuction(
            @PathVariable Long auctionId,
            @Valid @RequestBody AuctionEndRequestDto request,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("POST /api/auctions/{}/end - 경매 종료 요청 (관리자: {}, 중단: {})", 
                auctionId, adminId, request.isCancel());
        
        AuctionResponseDto response = auctionService.endAuction(auctionId, request, adminId);
        
        String message = request.isCancel() ? "경매가 중단되었습니다." : "경매가 종료되었습니다.";
        
        return ResponseEntity.ok(
            ResponseDto.success(response, message)
        );
    }
    
    /**
     * 경매 즉시 종료 (간편 API)
     * 
     * @param auctionId 종료할 경매 ID
     * @param authentication 현재 로그인한 관리자 정보
     * @return 종료된 경매 정보
     */
    @PostMapping("/{auctionId}/end-now")
    @Operation(summary = "경매 즉시 종료", 
               description = "버튼 클릭으로 경매를 즉시 정상 종료합니다.")
    public ResponseEntity<ResponseDto<AuctionResponseDto>> endAuctionNow(
            @PathVariable Long auctionId,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("POST /api/auctions/{}/end-now - 경매 즉시 종료 요청 (관리자: {})", 
                auctionId, adminId);
        
        // 즉시 정상 종료 요청 생성
        AuctionEndRequestDto request = new AuctionEndRequestDto();
        request.setImmediateEnd(true);
        request.setEndReason("관리자 즉시 종료");
        
        AuctionResponseDto response = auctionService.endAuction(auctionId, request, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "경매가 즉시 종료되었습니다.")
        );
    }
    
    /**
     * 경매 즉시 중단 (간편 API)
     * 
     * @param auctionId 중단할 경매 ID
     * @param authentication 현재 로그인한 관리자 정보
     * @return 중단된 경매 정보
     */
    @PostMapping("/{auctionId}/cancel-now")
    @Operation(summary = "경매 즉시 중단", 
               description = "버튼 클릭으로 경매를 즉시 중단합니다.")
    public ResponseEntity<ResponseDto<AuctionResponseDto>> cancelAuctionNow(
            @PathVariable Long auctionId,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("POST /api/auctions/{}/cancel-now - 경매 즉시 중단 요청 (관리자: {})", 
                auctionId, adminId);
        
        // 즉시 중단 요청 생성
        AuctionEndRequestDto request = new AuctionEndRequestDto();
        request.setImmediateEnd(true);
        request.setCancel(true);
        request.setEndReason("관리자 즉시 중단");
        
        AuctionResponseDto response = auctionService.endAuction(auctionId, request, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "경매가 즉시 중단되었습니다.")
        );
    }
    
    /**
     * 경매 목록 조회
     * 
     * @param pageable 페이징 정보
     * @param authentication 현재 로그인한 관리자 정보
     * @return 경매 목록
     */
    @GetMapping
    @Operation(summary = "경매 목록 조회", description = "전체 경매 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<Page<AuctionResponseDto>>> getAuctionList(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("GET /api/auctions - 경매 목록 조회 요청 (관리자: {})", adminId);
        
        Page<AuctionResponseDto> response = auctionService.getAuctionList(pageable, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "경매 목록을 성공적으로 조회했습니다.")
        );
    }
    
    /**
     * 진행 중인 경매 목록 조회
     * 
     * @param pageable 페이징 정보
     * @param authentication 현재 로그인한 관리자 정보
     * @return 진행 중인 경매 목록
     */
    @GetMapping("/active")
    @Operation(summary = "진행 중인 경매 목록 조회", description = "현재 진행 중인 경매 목록을 조회합니다.")
    public ResponseEntity<ResponseDto<Page<AuctionResponseDto>>> getActiveAuctions(
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("GET /api/auctions/active - 진행 중인 경매 목록 조회 요청 (관리자: {})", adminId);
        
        Page<AuctionResponseDto> response = auctionService.getActiveAuctions(pageable, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "진행 중인 경매 목록을 성공적으로 조회했습니다.")
        );
    }
    
    /**
     * 경매 상세 조회
     * 
     * @param auctionId 조회할 경매 ID
     * @param authentication 현재 로그인한 관리자 정보
     * @return 경매 상세 정보
     */
    @GetMapping("/{auctionId}")
    @Operation(summary = "경매 상세 조회", description = "특정 경매의 상세 정보를 조회합니다.")
    public ResponseEntity<ResponseDto<AuctionResponseDto>> getAuction(
            @PathVariable Long auctionId,
            Authentication authentication) {
        
        Long adminId = (Long) authentication.getPrincipal();
        log.info("GET /api/auctions/{} - 경매 상세 조회 요청 (관리자: {})", 
                auctionId, adminId);
        
        AuctionResponseDto response = auctionService.getAuction(auctionId, adminId);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "경매 정보를 성공적으로 조회했습니다.")
        );
    }
}