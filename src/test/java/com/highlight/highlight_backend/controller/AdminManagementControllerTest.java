package com.highlight.highlight_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.highlight.highlight_backend.dto.AdminCreateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
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

    @Test
    @DisplayName("관리자 계정 생성 성공 테스트 - SUPER_ADMIN 권한")
    @WithMockUser(username = "admin", authorities = "SUPER_ADMIN")
    void testCreateAdminSuccess() throws Exception {
        // Given
        AdminCreateRequestDto request = createValidAdminCreateRequest();

        // When & Then
        mockMvc.perform(post("/api/admin/admin-management/admins")
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
    @WithMockUser(username = "manager", authorities = "ADMIN")
    void testCreateAdminFailWithInsufficientPermission() throws Exception {
        // Given
        AdminCreateRequestDto request = createValidAdminCreateRequest();

        // When & Then  
        mockMvc.perform(post("/api/admin/admin-management/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 계정 생성 실패 테스트 - 필수 필드 누락")
    @WithMockUser(username = "admin", authorities = "SUPER_ADMIN")
    void testCreateAdminFailWithMissingFields() throws Exception {
        // Given - 빈 요청 객체
        AdminCreateRequestDto request = new AdminCreateRequestDto();

        // When & Then
        mockMvc.perform(post("/api/admin/admin-management/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("관리자 목록 조회 성공 테스트")
    @WithMockUser(username = "admin", authorities = "SUPER_ADMIN")
    void testGetAdminListSuccess() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/admin-management/admins")
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
    @WithMockUser(username = "manager", authorities = "ADMIN")
    void testGetAdminListFailWithInsufficientPermission() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/admin-management/admins")
                .param("page", "0")
                .param("size", "20"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 상세 조회 성공 테스트")
    @WithMockUser(username = "admin", authorities = "SUPER_ADMIN")
    void testGetAdminSuccess() throws Exception {
        // Given: 데이터베이스에서 실제 존재하는 manager 관리자의 ID 조회 (16)
        Long adminId = 16L;

        // When & Then
        mockMvc.perform(get("/api/admin/admin-management/admins/{adminId}", adminId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(adminId.intValue()))
                .andExpect(jsonPath("$.message").value("관리자 정보를 성공적으로 조회했습니다."));
    }

    @Test
    @DisplayName("관리자 상세 조회 실패 테스트 - 존재하지 않는 관리자")
    @WithMockUser(username = "admin", authorities = "SUPER_ADMIN")
    void testGetAdminFailWithNonExistentAdmin() throws Exception {
        // Given
        Long nonExistentAdminId = 99999L;

        // When & Then
        mockMvc.perform(get("/api/admin/admin-management/admins/{adminId}", nonExistentAdminId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("관리자 계정 삭제 성공 테스트")
    @WithMockUser(username = "admin", authorities = "SUPER_ADMIN")
    void testDeleteAdminSuccess() throws Exception {
        // Given: 먼저 관리자 계정 생성
        AdminCreateRequestDto createRequest = createValidAdminCreateRequest();
        String response = mockMvc.perform(post("/api/admin/admin-management/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 생성된 관리자 ID 추출
        Long adminId = objectMapper.readTree(response).get("data").get("id").asLong();

        // When & Then
        mockMvc.perform(delete("/api/admin/admin-management/admins/{adminId}", adminId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("관리자 계정이 성공적으로 삭제되었습니다."));
    }

    @Test
    @DisplayName("관리자 계정 삭제 실패 테스트 - 일반 관리자 권한")
    @WithMockUser(username = "manager", authorities = "ADMIN")
    void testDeleteAdminFailWithInsufficientPermission() throws Exception {
        // Given: 존재하는 관리자 ID 사용 (manager 자신을 삭제 시도)
        Long adminId = 16L;

        // When & Then
        mockMvc.perform(delete("/api/admin/admin-management/admins/{adminId}", adminId))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 계정 삭제 실패 테스트 - 존재하지 않는 관리자")
    @WithMockUser(username = "admin", authorities = "SUPER_ADMIN")
    void testDeleteAdminFailWithNonExistentAdmin() throws Exception {
        // Given
        Long nonExistentAdminId = 99999L;

        // When & Then
        mockMvc.perform(delete("/api/admin/admin-management/admins/{adminId}", nonExistentAdminId))
                .andDo(print())
                .andExpect(status().isNotFound());
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