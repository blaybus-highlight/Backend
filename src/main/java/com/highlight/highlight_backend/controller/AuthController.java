package com.highlight.highlight_backend.controller;

import com.highlight.highlight_backend.dto.LoginRequestDto;
import com.highlight.highlight_backend.dto.LoginResponseDto;
import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 컨트롤러
 * 
 * 백오피스 관리자 인증 관련 API를 제공합니다.
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "백오피스 관리자 로그인/인증 관련 API")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 관리자 로그인
     * 
     * @param request 로그인 요청 정보
     * @return 로그인 응답 (JWT 토큰 포함)
     */
    @PostMapping("/login")
    @Operation(summary = "관리자 로그인", description = "설정된 ID/PW로 백오피스 관리자 로그인을 수행합니다.")
    public ResponseEntity<ResponseDto<LoginResponseDto>> login(
            @Valid @RequestBody LoginRequestDto request) {
        
        log.info("POST /api/auth/login - 관리자 로그인 요청: {}", request.getAdminId());
        
        LoginResponseDto response = authService.login(request);
        
        return ResponseEntity.ok(
            ResponseDto.success(response, "로그인이 성공적으로 완료되었습니다.")
        );
    }
    
    /**
     * 토큰 유효성 검증
     * 
     * @param authorizationHeader Authorization 헤더
     * @return 검증 결과
     */
    @GetMapping("/validate")
    @Operation(summary = "토큰 유효성 검증", description = "JWT 토큰의 유효성을 검증합니다.")
    public ResponseEntity<ResponseDto<Boolean>> validateToken(
            @RequestHeader("Authorization") String authorizationHeader) {
        
        log.info("GET /api/auth/validate - 토큰 검증 요청");
        
        String token = authorizationHeader.replace("Bearer ", "");
        boolean isValid = authService.validateToken(token);
        
        return ResponseEntity.ok(
            ResponseDto.success(isValid, 
                isValid ? "유효한 토큰입니다." : "유효하지 않은 토큰입니다.")
        );
    }
    
    /**
     * 로그아웃 (클라이언트 측에서 토큰 삭제)
     * 
     * @return 로그아웃 응답
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "관리자 로그아웃을 수행합니다.")
    public ResponseEntity<ResponseDto<String>> logout() {
        
        log.info("POST /api/auth/logout - 로그아웃 요청");
        
        return ResponseEntity.ok(
            ResponseDto.success("SUCCESS", "로그아웃이 완료되었습니다.")
        );
    }
}