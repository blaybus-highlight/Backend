package com.highlight.highlight_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.highlight.highlight_backend.domain.Product;
import com.highlight.highlight_backend.dto.ProductCreateRequestDto;
import com.highlight.highlight_backend.dto.ProductUpdateRequestDto;
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
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * 상품 관리 API 테스트
 * 
 * @author 전우선
 * @since 2025.08.14
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductControllerTest {

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
    @DisplayName("상품 등록 성공 테스트 - 총관리자")
    void testCreateProductSuccess() throws Exception {
        // Given
        ProductCreateRequestDto request = createValidProductRequest();

        // When & Then
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productName").value("테스트 상품"))
                .andExpect(jsonPath("$.data.shortDescription").value("테스트 상품 소개"))
                .andExpect(jsonPath("$.data.startingPrice").value(100000))
                .andExpect(jsonPath("$.message").value("상품이 성공적으로 등록되었습니다."));
    }

    @Test
    @DisplayName("상품 등록 성공 테스트 - 일반관리자")
    void testCreateProductSuccessAsManager() throws Exception {
        // Given
        ProductCreateRequestDto request = createValidProductRequest();

        // When & Then
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + managerAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productName").value("테스트 상품"))
                .andExpect(jsonPath("$.message").value("상품이 성공적으로 등록되었습니다."));
    }

    @Test
    @DisplayName("상품 등록 실패 테스트 - 필수 필드 누락")
    void testCreateProductFailWithMissingFields() throws Exception {
        // Given
        ProductCreateRequestDto request = new ProductCreateRequestDto();
        // 필수 필드들을 설정하지 않음

        // When & Then
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("상품 등록 실패 테스트 - 인증 토큰 없음")
    void testCreateProductFailWithoutAuth() throws Exception {
        // Given
        ProductCreateRequestDto request = createValidProductRequest();

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("상품 목록 조회 성공 테스트")
    void testGetProductListSuccess() throws Exception {
        // Given: 먼저 상품을 등록
        ProductCreateRequestDto createRequest = createValidProductRequest();
        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)));

        // When & Then
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + adminAccessToken)
                .param("page", "0")
                .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.message").value("상품 목록을 성공적으로 조회했습니다."));
    }

    @Test
    @DisplayName("상품 상세 조회 성공 테스트")
    void testGetProductSuccess() throws Exception {
        // Given: 먼저 상품을 등록하고 ID를 얻음
        ProductCreateRequestDto createRequest = createValidProductRequest();
        MvcResult createResult = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // JSON에서 생성된 상품 ID 추출
        String createResponse = createResult.getResponse().getContentAsString();
        Integer productIdInt = JsonPath.read(createResponse, "$.data.id");
        Long productId = productIdInt.longValue();

        // When & Then
        mockMvc.perform(get("/api/products/{productId}", productId)
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(productId.intValue()))
                .andExpect(jsonPath("$.data.productName").value("테스트 상품"))
                .andExpect(jsonPath("$.message").value("상품 정보를 성공적으로 조회했습니다."));
    }

    @Test
    @DisplayName("상품 상세 조회 실패 테스트 - 존재하지 않는 상품")
    void testGetProductFailWithNonExistentProduct() throws Exception {
        // Given
        Long nonExistentProductId = 99999L;

        // When & Then
        mockMvc.perform(get("/api/products/{productId}", nonExistentProductId)
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("상품 수정 성공 테스트")
    void testUpdateProductSuccess() throws Exception {
        // Given: 먼저 상품을 등록하고 ID를 얻음
        ProductCreateRequestDto createRequest = createValidProductRequest();
        MvcResult createResult = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // JSON에서 생성된 상품 ID 추출
        String createResponse = createResult.getResponse().getContentAsString();
        Integer productIdInt = JsonPath.read(createResponse, "$.data.id");
        Long productId = productIdInt.longValue();
        
        ProductUpdateRequestDto updateRequest = createValidProductUpdateRequest();

        // When & Then
        mockMvc.perform(put("/api/products/{productId}", productId)
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productName").value("수정된 테스트 상품"))
                .andExpect(jsonPath("$.message").value("상품이 성공적으로 수정되었습니다."));
    }

    @Test
    @DisplayName("상품 삭제 성공 테스트")
    void testDeleteProductSuccess() throws Exception {
        // Given: 먼저 상품을 등록하고 ID를 얻음
        ProductCreateRequestDto createRequest = createValidProductRequest();
        MvcResult createResult = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // JSON에서 생성된 상품 ID 추출
        String createResponse = createResult.getResponse().getContentAsString();
        Integer productIdInt = JsonPath.read(createResponse, "$.data.id");
        Long productId = productIdInt.longValue();

        // When & Then
        mockMvc.perform(delete("/api/products/{productId}", productId)
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("상품이 성공적으로 삭제되었습니다."));
    }

    @Test
    @DisplayName("상품 삭제 실패 테스트 - 존재하지 않는 상품")
    void testDeleteProductFailWithNonExistentProduct() throws Exception {
        // Given
        Long nonExistentProductId = 99999L;

        // When & Then
        mockMvc.perform(delete("/api/products/{productId}", nonExistentProductId)
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /**
     * 유효한 상품 등록 요청 데이터 생성
     */
    private ProductCreateRequestDto createValidProductRequest() {
        ProductCreateRequestDto request = new ProductCreateRequestDto();
        
        // 리플렉션을 통해 private 필드 설정
        try {
            setField(request, "productName", "테스트 상품");
            setField(request, "shortDescription", "테스트 상품 소개");
            setField(request, "history", "테스트 상품의 역사입니다.");
            setField(request, "expectedEffects", "테스트 상품의 기대효과입니다.");
            setField(request, "detailedInfo", "테스트 상품의 상세 정보입니다.");
            setField(request, "startingPrice", new BigDecimal("100000"));
            setField(request, "category", Product.Category.PROPS);
            setField(request, "productCount", 1L);
            setField(request, "material", "도자기");
            setField(request, "size", "10x20x5");
            setField(request, "brand", "NAFAL");
            setField(request, "manufactureYear", 2020);
            setField(request, "condition", "상태 양호");
            setField(request, "rank", Product.ProductRank.BEST);
            setField(request, "images", new ArrayList<>());
            setField(request, "isDraft", false);
        } catch (Exception e) {
            throw new RuntimeException("테스트 데이터 생성 실패", e);
        }
        
        return request;
    }

    /**
     * 유효한 상품 수정 요청 데이터 생성
     */
    private ProductUpdateRequestDto createValidProductUpdateRequest() {
        ProductUpdateRequestDto request = new ProductUpdateRequestDto();
        
        // 리플렉션을 통해 private 필드 설정
        try {
            setField(request, "productName", "수정된 테스트 상품");
            setField(request, "shortDescription", "수정된 상품 소개");
            setField(request, "history", "수정된 상품의 역사입니다.");
            setField(request, "expectedEffects", "수정된 상품의 기대효과입니다.");
            setField(request, "detailedInfo", "수정된 상품의 상세 정보입니다.");
            setField(request, "startingPrice", new BigDecimal("150000"));
            setField(request, "category", Product.Category.FURNITURE);
            setField(request, "productCount", 2L);
            setField(request, "material", "목재");
            setField(request, "size", "50x100x30");
            setField(request, "brand", "NAFAL2");
            setField(request, "manufactureYear", 2022);
            setField(request, "condition", "새상품급");
            setField(request, "rank", Product.ProductRank.GREAT);
            setField(request, "images", new ArrayList<>());
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