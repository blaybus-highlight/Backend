package com.highlight.highlight_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.highlight.highlight_backend.dto.AuctionEndRequestDto;
import com.highlight.highlight_backend.dto.AuctionScheduleRequestDto;
import com.highlight.highlight_backend.dto.AuctionStartRequestDto;
import com.highlight.highlight_backend.domain.Product;
import com.highlight.highlight_backend.dto.ProductCreateRequestDto;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * 경매 관리 API 테스트
 * 
 * @author 전우선
 * @since 2025.08.14
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminAccessToken;
    private String managerAccessToken;

    @BeforeEach
    void setUp() throws Exception {
        // 총관리자 로그인하여 토큰 획득
        String adminLoginRequest = "{\"adminId\":\"admin\",\"password\":\"Admin123!@#\"}";
        MvcResult adminResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(adminLoginRequest))
                .andExpect(status().isOk())
                .andReturn();
        
        adminAccessToken = JsonPath.read(adminResult.getResponse().getContentAsString(), "$.data.accessToken");

        // 일반관리자 로그인하여 토큰 획득
        String managerLoginRequest = "{\"adminId\":\"manager\",\"password\":\"Manager456!@#\"}";
        MvcResult managerResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(managerLoginRequest))
                .andExpect(status().isOk())
                .andReturn();
        
        managerAccessToken = JsonPath.read(managerResult.getResponse().getContentAsString(), "$.data.accessToken");
    }

    @Test
    @DisplayName("경매 예약 성공 테스트")
    void testScheduleAuctionSuccess() throws Exception {
        // Given: 먼저 상품 등록
        Long productId = createTestProduct();
        AuctionScheduleRequestDto request = createValidScheduleRequest(productId);

        // When & Then
        mockMvc.perform(post("/api/auctions/schedule")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.product.id").value(productId.intValue()))
                .andExpect(jsonPath("$.message").value("경매가 성공적으로 예약되었습니다."));
    }

    @Test
    @DisplayName("경매 예약 실패 테스트 - 필수 필드 누락")
    void testScheduleAuctionFailWithMissingFields() throws Exception {
        // Given
        AuctionScheduleRequestDto request = new AuctionScheduleRequestDto();
        // 필수 필드들을 설정하지 않음

        // When & Then
        mockMvc.perform(post("/api/auctions/schedule")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("경매 시작 성공 테스트 - 즉시 시작")
    void testStartAuctionSuccessImmediate() throws Exception {
        // Given: 먼저 경매 예약
        Long auctionId = createTestAuction();
        AuctionStartRequestDto request = new AuctionStartRequestDto();
        request.setImmediateStart(true);

        // When & Then
        mockMvc.perform(post("/api/auctions/{auctionId}/start", auctionId)
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("경매가 성공적으로 시작되었습니다."));
    }

    @Test
    @DisplayName("경매 즉시 시작 테스트 (간편 API)")
    void testStartAuctionNow() throws Exception {
        // Given: 먼저 경매 예약
        Long auctionId = createTestAuction();

        // When & Then
        mockMvc.perform(post("/api/auctions/{auctionId}/start-now", auctionId)
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("경매가 즉시 시작되었습니다."));
    }

    @Test
    @DisplayName("경매 종료 성공 테스트")
    void testEndAuctionSuccess() throws Exception {
        // Given: 먼저 경매 예약 후 시작
        Long auctionId = createTestAuction();
        startTestAuction(auctionId);

        AuctionEndRequestDto request = new AuctionEndRequestDto();
        request.setImmediateEnd(true);
        request.setEndReason("테스트 종료");

        // When & Then
        mockMvc.perform(post("/api/auctions/{auctionId}/end", auctionId)
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("경매가 종료되었습니다."));
    }

    @Test
    @DisplayName("경매 즉시 종료 테스트 (간편 API)")
    void testEndAuctionNow() throws Exception {
        // Given: 먼저 경매 예약 후 시작
        Long auctionId = createTestAuction();
        startTestAuction(auctionId);

        // When & Then
        mockMvc.perform(post("/api/auctions/{auctionId}/end-now", auctionId)
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("경매가 즉시 종료되었습니다."));
    }

    @Test
    @DisplayName("경매 즉시 중단 테스트 (간편 API)")
    void testCancelAuctionNow() throws Exception {
        // Given: 먼저 경매 예약 후 시작
        Long auctionId = createTestAuction();
        startTestAuction(auctionId);

        // When & Then
        mockMvc.perform(post("/api/auctions/{auctionId}/cancel-now", auctionId)
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("경매가 즉시 중단되었습니다."));
    }

    @Test
    @DisplayName("경매 목록 조회 성공 테스트")
    void testGetAuctionListSuccess() throws Exception {
        // Given: 먼저 경매 예약
        createTestAuction();

        // When & Then
        mockMvc.perform(get("/api/auctions")
                .header("Authorization", "Bearer " + adminAccessToken)
                .param("page", "0")
                .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.message").value("경매 목록을 성공적으로 조회했습니다."));
    }

    @Test
    @DisplayName("진행 중인 경매 목록 조회 성공 테스트")
    void testGetActiveAuctionsSuccess() throws Exception {
        // Given: 먼저 경매 예약 후 시작
        Long auctionId = createTestAuction();
        startTestAuction(auctionId);

        // When & Then
        mockMvc.perform(get("/api/auctions/active")
                .header("Authorization", "Bearer " + adminAccessToken)
                .param("page", "0")
                .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.message").value("진행 중인 경매 목록을 성공적으로 조회했습니다."));
    }

    @Test
    @DisplayName("경매 상세 조회 성공 테스트")
    void testGetAuctionSuccess() throws Exception {
        // Given: 먼저 경매 예약
        Long auctionId = createTestAuction();

        // When & Then
        mockMvc.perform(get("/api/auctions/{auctionId}", auctionId)
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(auctionId.intValue()))
                .andExpect(jsonPath("$.message").value("경매 정보를 성공적으로 조회했습니다."));
    }

    @Test
    @DisplayName("경매 상세 조회 실패 테스트 - 존재하지 않는 경매")
    void testGetAuctionFailWithNonExistentAuction() throws Exception {
        // Given
        Long nonExistentAuctionId = 99999L;

        // When & Then
        mockMvc.perform(get("/api/auctions/{auctionId}", nonExistentAuctionId)
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /**
     * 테스트용 상품 생성
     */
    private Long createTestProduct() throws Exception {
        ProductCreateRequestDto request = new ProductCreateRequestDto();
        
        // 리플렉션을 통해 private 필드 설정
        setField(request, "productName", "테스트 경매 상품");
        setField(request, "shortDescription", "경매용 테스트 상품");
        setField(request, "history", "테스트 상품의 역사");
        setField(request, "expectedEffects", "테스트 상품의 기대효과");
        setField(request, "detailedInfo", "테스트 상품의 상세 정보");
        setField(request, "startingPrice", new BigDecimal("100000"));
        setField(request, "category", Product.Category.PROPS);
        setField(request, "productCount", 1L);
        setField(request, "material", "도자기");
        setField(request, "size", "15x25x10");
        setField(request, "brand", "NAFAL");
        setField(request, "manufactureYear", 2021);
        setField(request, "condition", "양호");
        setField(request, "rank", Product.ProductRank.GOOD);
        setField(request, "images", new ArrayList<>());
        setField(request, "isDraft", false);
        
        MvcResult result = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Integer productIdInt = JsonPath.read(response, "$.data.id");
        return productIdInt.longValue();
    }

    /**
     * 테스트용 경매 예약
     */
    private Long createTestAuction() throws Exception {
        Long productId = createTestProduct();
        AuctionScheduleRequestDto request = createValidScheduleRequest(productId);

        MvcResult result = mockMvc.perform(post("/api/auctions/schedule")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Integer auctionIdInt = JsonPath.read(response, "$.data.id");
        return auctionIdInt.longValue();
    }

    /**
     * 테스트용 경매 시작
     */
    private void startTestAuction(Long auctionId) throws Exception {
        mockMvc.perform(post("/api/auctions/{auctionId}/start-now", auctionId)
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk());
    }

    /**
     * 유효한 경매 예약 요청 데이터 생성
     */
    private AuctionScheduleRequestDto createValidScheduleRequest(Long productId) {
        AuctionScheduleRequestDto request = new AuctionScheduleRequestDto();
        
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(3);
        
        try {
            setField(request, "productId", productId);
            setField(request, "scheduledStartTime", startTime);
            setField(request, "scheduledEndTime", endTime);
            setField(request, "description", "테스트 경매 설명");
        } catch (Exception e) {
            throw new RuntimeException("테스트 데이터 생성 실패", e);
        }
        
        return request;
    }

    /**
     * 리플렉션을 통한 private 필드 설정 헬퍼 메서드
     */
    private void setField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}