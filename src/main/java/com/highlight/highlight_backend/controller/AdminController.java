package com.highlight.highlight_backend.controller;

import com.highlight.highlight_backend.dto.AdminSignUpRequestDto;
import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.service.AdminManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
        summary = "관리자 회원가입", 
        description = "백오피스 관리자 계정을 생성합니다. ID와 비밀번호만으로 간단하게 생성할 수 있으며, 기본적으로 ADMIN 권한이 부여됩니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "관리자 계정 생성 성공",
            content = @Content(schema = @Schema(implementation = ResponseDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 관리자 ID"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ResponseDto<String>> adminSignUp(
            @Parameter(description = "관리자 회원가입 요청 정보 (ID, 비밀번호)", required = true)
            @Valid @RequestBody AdminSignUpRequestDto signUpRequestDto) {
        log.info("POST /api/admin/signup - 관리자 회원가입: {}", signUpRequestDto.getAdminId());
        
        adminManagementService.simpleSignUp(signUpRequestDto);
        
        return ResponseEntity.ok(ResponseDto.success("SUCCESS", "관리자 계정이 성공적으로 생성되었습니다."));
    }
}