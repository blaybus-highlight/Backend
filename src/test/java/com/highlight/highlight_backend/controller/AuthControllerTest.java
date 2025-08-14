package com.highlight.highlight_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.highlight.highlight_backend.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 인증 컨트롤러 테스트
 * DB에 있는 실제 관리자 데이터로 로그인 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("총관리자 로그인 성공 테스트 - DB 데이터 사용")
    void testSuperAdminLoginSuccess() throws Exception {
        // Given - DB에 있는 admin 계정 사용
        String loginRequest = "{\"adminId\":\"admin\",\"password\":\"Admin123!@#\"}";

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.adminId").value("admin"))
                .andExpect(jsonPath("$.data.adminName").value("시스템 관리자"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    @DisplayName("일반관리자 로그인 성공 테스트 - DB 데이터 사용")
    void testAdminLoginSuccess() throws Exception {
        // Given - DB에 있는 manager 계정 사용
        String loginRequest = "{\"adminId\":\"manager\",\"password\":\"Manager456!@#\"}";

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.adminId").value("manager"))
                .andExpect(jsonPath("$.data.adminName").value("일반 관리자"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 실패 테스트")
    void testLoginFailWithWrongPassword() throws Exception {
        // Given
        String loginRequest = "{\"adminId\":\"admin\",\"password\":\"wrongpassword\"}";

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("ADMIN_002"));
    }

    @Test
    @DisplayName("존재하지 않는 관리자 ID로 로그인 실패 테스트")
    void testLoginFailWithNonExistentAdmin() throws Exception {
        // Given
        String loginRequest = "{\"adminId\":\"nonexistent\",\"password\":\"Admin123!@#\"}";

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("ADMIN_002"));
    }

    @Test
    @DisplayName("빈 요청으로 로그인 실패 테스트")
    void testLoginFailWithEmptyRequest() throws Exception {
        // Given
        String loginRequest = "{}";

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}