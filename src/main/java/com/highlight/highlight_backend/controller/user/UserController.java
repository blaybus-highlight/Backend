package com.highlight.highlight_backend.controller.user;

import com.highlight.highlight_backend.dto.ResponseDto;
import com.highlight.highlight_backend.dto.UserLoginRequestDto;
import com.highlight.highlight_backend.dto.UserLoginResponseDto;
import com.highlight.highlight_backend.dto.UserSignUpRequestDto;
import com.highlight.highlight_backend.service.UserSignUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 유저 관리 controller
 *
 * @author 탁찬홍
 * @Since 2025.08.15
 */
@Slf4j
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "일반 User API", description = "생성/조회/삭제 로직입니다.")
public class UserController {

    private final UserSignUpService userSignUpService;

    @PostMapping("/signup")
    @Operation(summary = "User 회원가입", description = "ID, PW, PhoneNumber, Nickname을 요구")
    public ResponseEntity<ResponseDto<UserSignUpRequestDto>> signUP(@Valid @RequestBody UserSignUpRequestDto signUpRequestDto) {
        log.info("POST /api/public/signup - User 회원가입 (비로그인 사용자도 접근 가능)");
        UserSignUpRequestDto response = userSignUpService.signUp(signUpRequestDto);

        return ResponseEntity.ok(ResponseDto.success(response, "User 회원가입에 성공하였습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<UserLoginResponseDto>> login(@Valid @RequestBody UserLoginRequestDto loginRequestDto) {
        log.info("POST /api/public/login - User 로그인 (비로그인 사용자도 접근 가능)");
        UserLoginResponseDto response = userSignUpService.login(loginRequestDto);
        return ResponseEntity.ok(ResponseDto.success(response, "User 로그인에 성공하였습니다."));
    }
}
