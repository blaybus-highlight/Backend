package com.highlight.highlight_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.highlight.highlight_backend.dto.AdminCreateRequestDto;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * 관리자 관리 API 테스트
 * 
 * @author 전우선
 * @since 2025.08.14
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AdminManagementControllerTest {

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
    @DisplayName("관리자 계정 생성 성공 테스트 - SUPER_ADMIN")
    void testCreateAdminSuccess() throws Exception {
        // Given
        AdminCreateRequestDto request = createValidAdminCreateRequest();

        // When & Then
        mockMvc.perform(post("/api/admin-management/admins")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.adminId").value("testadmin"))
                .andExpect(jsonPath("$.data.adminName").value("테스트 관리자"))
                .andExpect(jsonPath("$.data.email").value("testadmin@test.com"))
                .andExpect(jsonPath("$.message").value("관리자 계정이 성공적으로 생성되었습니다."));
    }

    @Test
    @DisplayName("관리자 계정 생성 실패 테스트 - 일반 관리자 권한")
    void testCreateAdminFailWithInsufficientPermission() throws Exception {
        // Given
        AdminCreateRequestDto request = createValidAdminCreateRequest();

        // When & Then
        mockMvc.perform(post("/api/admin-management/admins")
                .header("Authorization", "Bearer " + managerAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 계정 생성 실패 테스트 - 필수 필드 누락")
    void testCreateAdminFailWithMissingFields() throws Exception {
        // Given
        AdminCreateRequestDto request = new AdminCreateRequestDto();
        // 필수 필드들을 설정하지 않음

        // When & Then
        mockMvc.perform(post("/api/admin-management/admins")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("관리자 목록 조회 성공 테스트")
    void testGetAdminListSuccess() throws Exception {
        // Given: 먼저 관리자 계정 생성
        AdminCreateRequestDto createRequest = createValidAdminCreateRequest();
        mockMvc.perform(post("/api/admin-management/admins")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)));

        // When & Then
        mockMvc.perform(get("/api/admin-management/admins")
                .header("Authorization", "Bearer " + adminAccessToken)
                .param("page", "0")
                .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.message").value("관리자 목록을 성공적으로 조회했습니다."));
    }

    @Test
    @DisplayName("관리자 목록 조회 실패 테스트 - 일반 관리자 권한")
    void testGetAdminListFailWithInsufficientPermission() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin-management/admins")
                .header("Authorization", "Bearer " + managerAccessToken)
                .param("page", "0")
                .param("size", "20"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 상세 조회 성공 테스트")
    void testGetAdminSuccess() throws Exception {
        // Given: 먼저 관리자 계정 생성하고 ID 얻기
        Long adminId = createTestAdmin();

        // When & Then
        mockMvc.perform(get("/api/admin-management/admins/{adminId}", adminId)
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(adminId.intValue()))
                .andExpect(jsonPath("$.data.adminId").value("testadmin"))
                .andExpect(jsonPath("$.message").value("관리자 정보를 성공적으로 조회했습니다."));
    }

    @Test
    @DisplayName("관리자 상세 조회 실패 테스트 - 존재하지 않는 관리자")
    void testGetAdminFailWithNonExistentAdmin() throws Exception {
        // Given
        Long nonExistentAdminId = 99999L;

        // When & Then
        mockMvc.perform(get("/api/admin-management/admins/{adminId}", nonExistentAdminId)
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("관리자 계정 삭제 성공 테스트")
    void testDeleteAdminSuccess() throws Exception {
        // Given: 먼저 관리자 계정 생성
        Long adminId = createTestAdmin();

        // When & Then
        mockMvc.perform(delete("/api/admin-management/admins/{adminId}", adminId)
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("관리자 계정이 성공적으로 삭제되었습니다."));
    }

    @Test
    @DisplayName("관리자 계정 삭제 실패 테스트 - 일반 관리자 권한")
    void testDeleteAdminFailWithInsufficientPermission() throws Exception {
        // Given
        Long adminId = createTestAdmin();

        // When & Then
        mockMvc.perform(delete("/api/admin-management/admins/{adminId}", adminId)
                .header("Authorization", "Bearer " + managerAccessToken))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 계정 삭제 실패 테스트 - 존재하지 않는 관리자")
    void testDeleteAdminFailWithNonExistentAdmin() throws Exception {
        // Given
        Long nonExistentAdminId = 99999L;

        // When & Then
        mockMvc.perform(delete("/api/admin-management/admins/{adminId}", nonExistentAdminId)
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /**
     * 테스트용 관리자 계정 생성
     */
    private Long createTestAdmin() throws Exception {
        AdminCreateRequestDto request = createValidAdminCreateRequest();

        MvcResult result = mockMvc.perform(post("/api/admin-management/admins")
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Integer adminIdInt = JsonPath.read(response, "$.data.id");
        return adminIdInt.longValue();
    }

    /**
     * 유효한 관리자 생성 요청 데이터 생성
     */
    private AdminCreateRequestDto createValidAdminCreateRequest() {
        AdminCreateRequestDto request = new AdminCreateRequestDto();
        
        try {
            setField(request, "adminId", "testadmin");
            setField(request, "password", "TestPass123!@#");
            setField(request, "adminName", "테스트 관리자");
            setField(request, "email", "testadmin@test.com");
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