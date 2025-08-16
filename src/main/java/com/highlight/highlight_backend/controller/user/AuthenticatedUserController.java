package com.highlight.highlight_backend.controller.user;

import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.dto.UserDetailResponseDto;
import com.highlight.highlight_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "사용자 마이페이지", description = "로그인한 사용자의 개인정보 조회 및 탈퇴 API")
public class AuthenticatedUserController {

    private final UserService userService;

    @GetMapping("/")
    @Operation(
        summary = "내 정보 조회", 
        description = "JWT 토큰으로 인증된 사용자의 상세 정보를 조회합니다. 개인정보(ID, 닉네임, 휴대폰번호 등)와 계정 상태를 포함합니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "내 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = ResponseDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰"),
        @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ResponseDto<UserDetailResponseDto>> getMyDetails(
            @Parameter(description = "JWT 인증 정보", hidden = true)
            Authentication authentication) {
        log.info("GET /api/user/ - 내 정보 조회");
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        UserDetailResponseDto response = userService.getUserDetailsById(userId);
        return ResponseEntity.ok(ResponseDto.success(response, "내 정보 조회에 성공하였습니다."));
    }

    @DeleteMapping("/")
    @Operation(
        summary = "회원 탈퇴", 
        description = "JWT 토큰으로 인증된 사용자가 본인 계정을 탈퇴(비활성화)합니다. 탈퇴 후에는 로그인할 수 없으며, 개인정보는 관련 법령에 따라 처리됩니다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "회원 탈퇴 성공",
            content = @Content(schema = @Schema(implementation = ResponseDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰"),
        @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
        @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "진행 중인 경매가 있어 탈퇴 불가"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ResponseDto<?>> deleteUser(
            @Parameter(description = "JWT 인증 정보", hidden = true)
            Authentication authentication) {
        log.info("DELETE /api/user/ - 회원 탈퇴");
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        userService.deleteUser(userId);
        return ResponseEntity.ok(ResponseDto.success(null, "회원 탈퇴 처리가 완료되었습니다."));
    }
}
