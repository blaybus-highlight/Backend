package com.highlight.highlight_backend.controller;

import com.highlight.highlight_backend.dto.AdminSignUpRequestDto;
import com.highlight.highlight_backend.dto.LoginRequestDto;
import com.highlight.highlight_backend.dto.LoginResponseDto;
import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.service.AdminManagementService;
import com.highlight.highlight_backend.service.AuthService;
import com.highlight.highlight_backend.util.ResponseUtils;
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
    private final AuthService authService;
    
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
        
        return ResponseUtils.successWithMessage("관리자 계정이 성공적으로 생성되었습니다.");
    }
    
    /**
     * 관리자 로그인
     * 
     * @param loginRequestDto 로그인 요청 정보
     * @return 로그인 응답 (JWT 토큰 포함)
     */
    @PostMapping("/login")
    @Operation(
        summary = "관리자 로그인", 
        description = "관리자 ID와 비밀번호로 로그인하여 JWT 액세스 토큰과 리프레시 토큰을 발급받습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = ResponseDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 올바르지 않음"),
        @ApiResponse(responseCode = "403", description = "비활성화된 관리자 계정"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ResponseDto<LoginResponseDto>> adminLogin(
            @Parameter(description = "관리자 로그인 요청 정보 (ID, 비밀번호)", required = true)
            @Valid @RequestBody LoginRequestDto loginRequestDto) {
        log.info("POST /api/admin/login - 관리자 로그인 시도: {}", loginRequestDto.getAdminId());
        
        LoginResponseDto response = authService.login(loginRequestDto);
        
        return ResponseUtils.success(response, "로그인이 성공적으로 완료되었습니다.");
    }
}