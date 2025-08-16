package com.highlight.highlight_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 즉시구매 요청 DTO
 * 
 * 사용자가 경매 상품을 즉시구매할 때 사용하는 요청 데이터입니다.
 * 
 * @author 전우선
 * @since 2025.08.16
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "즉시구매 요청 DTO")
public class BuyItNowRequestDto {
    
    /**
     * 즉시구매 확인 여부
     */
    @NotNull(message = "즉시구매 확인은 필수입니다")
    @Schema(description = "즉시구매 확인 여부", example = "true", required = true)
    private Boolean confirmed;
    
    /**
     * 결제 방법 (추후 결제 시스템 연동 시 사용)
     */
    @Schema(description = "결제 방법", example = "CREDIT_CARD")
    private String paymentMethod;
    
    /**
     * 배송 주소 ID (추후 배송 시스템 연동 시 사용)
     */
    @Schema(description = "배송 주소 ID", example = "1")
    private Long shippingAddressId;
}