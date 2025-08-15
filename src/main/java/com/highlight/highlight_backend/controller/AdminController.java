package com.highlight.highlight_backend.controller;

import com.highlight.highlight_backend.dto.AdminSignUpRequestDto;
import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.service.AdminManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 공용 API 컨트롤러
 * 
 * @author 전우선
 * @since 2025.08.15
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 인증", description = "관리자 회원가입, 로그인 API")
public class AdminController {
    
    private final AdminManagementService adminManagementService;
    
    /**
     * 관리자 간단 회원가입
     * 
     * @param signUpRequestDto 회원가입 정보 (ID, 비밀번호만)
     * @return 생성된 관리자 정보
     */
    @PostMapping("/signup")
    @Operation(summary = "관리자 회원가입", description = "ID, 비밀번호만으로 관리자 계정 생성")
    public ResponseEntity<ResponseDto<String>> adminSignUp(@Valid @RequestBody AdminSignUpRequestDto signUpRequestDto) {
        log.info("POST /api/admin/signup - 관리자 회원가입: {}", signUpRequestDto.getAdminId());
        
        adminManagementService.simpleSignUp(signUpRequestDto);
        
        return ResponseEntity.ok(ResponseDto.success("SUCCESS", "관리자 계정이 성공적으로 생성되었습니다."));
    }
}