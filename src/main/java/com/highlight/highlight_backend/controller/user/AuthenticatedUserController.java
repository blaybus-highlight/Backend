package com.highlight.highlight_backend.controller.user;

import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.dto.UserDetailResponseDto;
import com.highlight.highlight_backend.service.UserSignUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "인증된 User API", description = "인증이 필요한 User API입니다.")
public class AuthenticatedUserController {

    private final UserSignUpService userSignUpService;

    @GetMapping("/myPage")
    @Operation(summary = "내 정보 조회", description = "인증된 사용자의 상세 정보를 조회합니다.")
    public ResponseEntity<ResponseDto<UserDetailResponseDto>> getMyDetails(Authentication authentication) {
        log.info("GET /api/user/myPage - 내 정보 조회");
        Long userId = (Long) authentication.getPrincipal();
        UserDetailResponseDto response = userSignUpService.getUserDetailsById(userId);
        return ResponseEntity.ok(ResponseDto.success(response, "내 정보 조회에 성공하였습니다."));
    }
}
