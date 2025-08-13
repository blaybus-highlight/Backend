package com.highlight.highlight_backend.service;

import com.highlight.highlight_backend.domain.Admin;
import com.highlight.highlight_backend.dto.LoginRequestDto;
import com.highlight.highlight_backend.dto.LoginResponseDto;
import com.highlight.highlight_backend.exception.BusinessException;
import com.highlight.highlight_backend.exception.ErrorCode;
import com.highlight.highlight_backend.repository.AdminRepository;
import com.highlight.highlight_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 인증 서비스
 * 
 * 백오피스 관리자 로그인 및 인증 처리를 담당하는 서비스입니다.
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * 관리자 로그인 처리
     * 
     * @param request 로그인 요청 정보
     * @return 로그인 응답 정보 (JWT 토큰 포함)
     */
    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        log.info("관리자 로그인 시도: {}", request.getAdminId());
        
        // 1. 관리자 계정 조회
        Admin admin = adminRepository.findByAdminIdAndIsActiveTrue(request.getAdminId())
            .orElseThrow(() -> {
                log.warn("존재하지 않는 관리자 ID: {}", request.getAdminId());
                return new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
            });
        
        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            log.warn("잘못된 비밀번호 입력: {}", request.getAdminId());
            throw new BusinessException(ErrorCode.INVALID_LOGIN_CREDENTIALS);
        }
        
        // 3. 마지막 로그인 시간 업데이트
        admin.setLastLoginAt(LocalDateTime.now());
        adminRepository.save(admin);
        
        // 4. JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(admin.getId(), admin.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(admin.getId(), admin.getEmail());
        
        log.info("관리자 로그인 성공: {} (ID: {})", admin.getAdminName(), admin.getAdminId());
        
        return new LoginResponseDto(
            accessToken,
            refreshToken,
            admin.getAdminId(),
            admin.getAdminName(),
            "로그인이 성공적으로 완료되었습니다."
        );
    }
    
    /**
     * 토큰 유효성 검증
     * 
     * @param token JWT 토큰
     * @return 유효성 여부
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
    
    /**
     * 토큰에서 관리자 정보 조회
     * 
     * @param token JWT 토큰
     * @return 관리자 정보
     */
    public Admin getAdminFromToken(String token) {
        Long adminId = jwtUtil.getUserId(token);
        return adminRepository.findById(adminId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));
    }
}