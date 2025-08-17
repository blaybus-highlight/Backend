package com.highlight.highlight_backend.controller;

import com.highlight.highlight_backend.exception.CommonErrorCode;
import com.highlight.highlight_backend.exception.ErrorCode;
import com.highlight.highlight_backend.service.WebSocketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

/**
 * WebSocket 메시지 처리 컨트롤러
 * 
 * 실시간 경매 정보 전송을 위한 WebSocket 메시지 처리를 담당합니다.
 * 
 * @author 전우선
 * @since 2025.08.15
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "WebSocket 실시간 통신", description = "실시간 경매 정보 및 입찰 알림을 위한 WebSocket API")
public class WebSocketController {
    
    private final WebSocketService webSocketService;
    
    /**
     * 특정 경매 구독 처리
     * 
     * 클라이언트가 특정 경매의 실시간 정보를 구독할 때 호출됩니다.
     * 
     * @param auctionId 구독할 경매 ID
     */
    @SubscribeMapping("/topic/auction/{auctionId}")
    @Operation(
        summary = "특정 경매 실시간 구독", 
        description = "클라이언트가 특정 경매의 실시간 정보(입찰 현황, 가격 변동, 종료 시간 등)를 구독합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "구독 성공"),
        @ApiResponse(responseCode = "404", description = "경매를 찾을 수 없음")
    })
    public void subscribeToAuction(
            @Parameter(description = "구독할 경매의 고유 ID", required = true, example = "1")
            @DestinationVariable Long auctionId) {
        log.info("WebSocket - 경매 구독: 경매ID={}", auctionId);
        
        // 연결 성공 메시지 전송
        webSocketService.sendConnectionEstablished(auctionId);
    }
    
    /**
     * 전체 경매 목록 구독 처리
     * 
     * 경매 시작/종료 등 전반적인 경매 상태 변화를 구독할 때 사용됩니다.
     */
    @SubscribeMapping("/topic/auctions")
    @Operation(summary = "전체 경매 목록 구독", description = "경매 시작/종료 등 전반적인 경매 상태 변화를 구독합니다.")
    public void subscribeToAuctions() {
        log.info("WebSocket - 전체 경매 목록 구독");
        // 필요시 전체 경매 현황 전송 로직 추가 가능
    }
    
    /**
     * 경매 상태 요청 처리
     * 
     * 클라이언트가 특정 경매의 현재 상태를 요청할 때 처리합니다.
     * 
     * @param auctionId 상태를 요청하는 경매 ID
     */
    @MessageMapping("/auction/{auctionId}/status")
    @SendTo("/topic/auction/{auctionId}")
    @Operation(summary = "경매 상태 요청", description = "클라이언트가 특정 경매의 현재 상태를 요청합니다.")
    public void requestAuctionStatus(
            @Parameter(description = "상태를 요청하는 경매 ID", required = true)
            @DestinationVariable Long auctionId) {
        log.info("WebSocket - 경매 상태 요청: 경매ID={}", auctionId);
        
        try {
            // 실시간 경매 상태 조회 및 전송 (기존 REST API 로직 활용)
            // 여기서는 상태 업데이트를 트리거하는 역할
            log.info("WebSocket - 경매 상태 요청 처리 완료: 경매ID={}", auctionId);
        } catch (Exception e) {
            log.error("WebSocket - 경매 상태 요청 처리 실패: 경매ID={}, 오류={}", auctionId, e.getMessage());
            webSocketService.sendErrorMessage(auctionId, CommonErrorCode.WEBSOCKET_MESSAGE_SEND_FAILED);
        }
    }
}