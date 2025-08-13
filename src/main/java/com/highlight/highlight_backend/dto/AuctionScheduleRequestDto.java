package com.highlight.highlight_backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 경매 예약 요청 DTO
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Getter
@NoArgsConstructor
public class AuctionScheduleRequestDto {
    
    /**
     * 경매할 상품 ID
     */
    @NotNull(message = "상품 ID는 필수입니다")
    private Long productId;
    
    /**
     * 경매 시작 예정 시간
     */
    @NotNull(message = "경매 시작 시간은 필수입니다")
    @Future(message = "경매 시작 시간은 현재 시간 이후여야 합니다")
    private LocalDateTime scheduledStartTime;
    
    /**
     * 경매 종료 예정 시간
     */
    @NotNull(message = "경매 종료 시간은 필수입니다")
    @Future(message = "경매 종료 시간은 현재 시간 이후여야 합니다")
    private LocalDateTime scheduledEndTime;
    
    /**
     * 경매 설명/메모
     */
    @Size(max = 500, message = "경매 설명은 500자를 초과할 수 없습니다")
    private String description;
}