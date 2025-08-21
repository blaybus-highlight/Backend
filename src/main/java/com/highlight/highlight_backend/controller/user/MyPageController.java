package com.highlight.highlight_backend.controller.user;

import com.highlight.highlight_backend.dto.MyPageResponseDto;
import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 마이페이지 컨트롤러
 *
 * @author 탁찬홍
 * @since 2025.08.21
 */
@Slf4j
@RestController
@RequestMapping("/api/user/mypage")
@RequiredArgsConstructor
@Tag(name = "마이페이지", description = "사용자 마이페이지 정보 조회 API")
public class MyPageController {

    private final UserService userService;

    /**
     * 마이페이지 정보 조회
     * 
     * @param authentication 인증 정보
     * @return 마이페이지 정보
     */
    @GetMapping
    @Operation(
        summary = "마이페이지 정보 조회", 
        description = "현재 로그인한 사용자의 마이페이지 정보를 조회합니다. 등급, 포인트, 참여 횟수 등의 정보를 포함합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", 
            description = "마이페이지 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = ResponseDto.class))
        ),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<ResponseDto<MyPageResponseDto>> getMyPageInfo(
            @Parameter(hidden = true) Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.info("마이페이지 정보 조회 요청: 사용자ID={}", currentUserId);
        
        MyPageResponseDto myPageInfo = userService.getMyPageInfo(currentUserId);
        
        log.info("마이페이지 정보 조회 완료: 사용자ID={}, 등급={}, 참여횟수={}", 
                currentUserId, myPageInfo.getRank(), myPageInfo.getParticipationCount());
        
        return ResponseEntity.ok(ResponseDto.success(myPageInfo));
    }
    
    /**
     * Authentication에서 현재 사용자 ID를 추출
     *
     * @param authentication 인증 정보
     * @return 사용자 ID
     */
    private Long getCurrentUserId(Authentication authentication) {
        return (Long) authentication.getPrincipal();
    }
}
