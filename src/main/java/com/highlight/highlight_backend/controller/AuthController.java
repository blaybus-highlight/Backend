package com.highlight.highlight_backend.controller;

import com.highlight.highlight_backend.dto.LoginRequestDto;
import com.highlight.highlight_backend.dto.LoginResponseDto;
import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.service.AuthService;
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
 * 인증 컨트롤러
 * 
 * 백오피스 관리자 인증 관련 API를 제공합니다.
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Slf4j
@RestController
@RequestMapping("/api/admin-auth")
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
    @Operation(
        summary = "관리자 로그인", 
        description = "설정된 ID/PW로 백오피스 관리자 로그인을 수행합니다. 성공 시 JWT 액세스 토큰과 리프레시 토큰을 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = ResponseDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패 - 잘못된 ID 또는 비밀번호"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ResponseDto<LoginResponseDto>> login(
            @Parameter(description = "관리자 로그인 요청 정보 (ID, 비밀번호)", required = true)
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
    @Operation(
        summary = "토큰 유효성 검증", 
        description = "Authorization 헤더의 JWT 토큰 유효성을 검증합니다. Bearer 토큰 형식이어야 합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "토큰 검증 완료",
            content = @Content(schema = @Schema(implementation = ResponseDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 토큰 형식"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ResponseDto<Boolean>> validateToken(
            @Parameter(
                description = "JWT 토큰을 포함한 Authorization 헤더", 
                required = true,
                example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
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
    @Operation(
        summary = "로그아웃", 
        description = "관리자 로그아웃을 수행합니다. 클라이언트에서 토큰을 삭제해야 합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "로그아웃 성공",
            content = @Content(schema = @Schema(implementation = ResponseDto.class))
        ),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ResponseDto<String>> logout() {
        
        log.info("POST /api/auth/logout - 로그아웃 요청");
        
        return ResponseEntity.ok(
            ResponseDto.success("SUCCESS", "로그아웃이 완료되었습니다.")
        );
    }
}