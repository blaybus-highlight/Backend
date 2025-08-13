package com.highlight.highlight_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 DTO
 * 
 * 관리자 로그인을 위한 ID/PW 입력 데이터를 담는 클래스입니다.
 * 
 * @author 전우선
 * @since 2025.08.13
 */
@Getter
@NoArgsConstructor
public class LoginRequestDto {
    
    /**
     * 관리자 ID (이메일 또는 아이디)
     */
    private String adminId;
    
    /**
     * 관리자 비밀번호
     */
    private String password;
}