package com.highlight.highlight_backend.controller.user;

import com.highlight.highlight_backend.dto.*;
import com.highlight.highlight_backend.dto.PhoneVerificationRequestCodeDto;
import com.highlight.highlight_backend.dto.PhoneVerificationRequestDto;
import com.highlight.highlight_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@Tag(name = "사용자 인증", description = "회원가입, 로그인, 휴대폰 인증 API")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "User 회원가입", description = "ID, PW, PhoneNumber, Nickname을 요구")
    public ResponseEntity<ResponseDto<UserSignUpRequestDto>> signUP(@Valid @RequestBody UserSignUpRequestDto signUpRequestDto) {
        log.info("POST /api/public/signup - User 회원가입");
        UserSignUpRequestDto response = userService.signUp(signUpRequestDto);
        return ResponseEntity.ok(ResponseDto.success(response, "User 회원가입에 성공하였습니다."));
    }

    @PostMapping("/login")
    @Operation(summary = "User 로그인", description = "ID, PW 로그인 후 JWT 토큰 발급")
    public ResponseEntity<ResponseDto<UserLoginResponseDto>> login(@Valid @RequestBody UserLoginRequestDto loginRequestDto) {
        log.info("POST /api/public/login - User 로그인");
        UserLoginResponseDto response = userService.login(loginRequestDto);
        return ResponseEntity.ok(ResponseDto.success(response, "User 로그인에 성공하였습니다."));
    }

    @PostMapping("/request-phone-verification")
    @Operation(summary = "휴대폰 인증 코드 요청", description = "사용자의 휴대폰 번호로 인증 코드를 발송합니다.")
    public ResponseEntity<ResponseDto<?>> requestPhoneVerification(@Valid @RequestBody PhoneVerificationRequestCodeDto requestDto) {
        log.info("POST /api/public/request-phone-verification - 휴대폰 인증 코드 요청");
        userService.requestPhoneVerification(requestDto);
        return ResponseEntity.ok(ResponseDto.success(null, "인증 코드 발송에 성공하였습니다."));
    }

    @PostMapping("/verify-phone")
    @Operation(summary = "휴대폰 인증 코드 확인", description = "휴대폰 번호와 인증코드를 받아 인증 처리합니다.")
    public ResponseEntity<ResponseDto<?>> verifyPhone(@Valid @RequestBody PhoneVerificationRequestDto requestDto) {
        log.info("POST /api/public/verify-phone - 휴대폰 인증 코드 확인");
        userService.verifyPhoneNumber(requestDto);
        return ResponseEntity.ok(ResponseDto.success(null, "휴대폰 인증에 성공하였습니다."));
    }
}

    
