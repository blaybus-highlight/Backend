package com.highlight.highlight_backend.config;

import com.highlight.highlight_backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정
 * 
 * 애플리케이션의 보안 정책을 정의합니다.
 * JWT 기반 인증, CORS 설정, 엔드포인트 접근 권한을 구성합니다.
 * 
 * @author 전우선
 * @since 2025.08.08
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Spring Security 필터 체인 설정
     * 
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF 보호 비활성화 (JWT 사용하므로)
                .csrf(AbstractHttpConfigurer::disable)
                
                // CORS 설정 적용 (별도 CorsConfig에서 관리)
                .cors(cors -> {})
                
                // 보안 헤더 설정
                .headers(headers -> headers
                    .frameOptions(frameOptions -> frameOptions.deny())  // X-Frame-Options: DENY
                    .contentTypeOptions(contentTypeOptions -> {})  // X-Content-Type-Options: nosniff
                    .httpStrictTransportSecurity(hsts -> hsts
                        .maxAgeInSeconds(31536000)  // 1년
                        .includeSubDomains(true)
                    )
                )
                
                // 세션 사용하지 않음 (JWT 사용)
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 엔드포인트 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                    // 공개 엔드포인트 (인증 불필요)
                    .requestMatchers(
                        "/api/auth/**",                    // 인증 관련 API (일반 사용자)
                        "/api/admin-auth/**",              // 관리자 인증 관련 API
                        "/api/admin/signup",               // 관리자 회원가입
                        "/api/public/**",                  // 공개 API
                        "/api/auctions/*/bids",            // 경매 입찰 내역 조회 (공개)
                        "/api/auctions/*/status",          // 실시간 경매 상태 조회 (공개)
                        "/ws/**",                          // WebSocket 엔드포인트
                        "/topic/**",                       // WebSocket 토픽
                        "/queue/**",                       // WebSocket 큐
                        "/swagger-ui/**",                  // Swagger UI
                        "/api-docs/**",                    // API 문서
                        "/v3/api-docs/**",                 // OpenAPI 문서
                        "/error"                           // 에러 페이지
                    ).permitAll()
                    
                    // 사용자 인증 필요 엔드포인트
                    .requestMatchers(
                        "/api/bids",                       // 입찰 참여
                        "/api/users/bids",                 // 내 입찰 내역
                        "/api/users/wins"                  // 내 낙찰 내역
                    ).authenticated()
                    
                    // 관리자 권한 필요 엔드포인트  
                    .requestMatchers(
                        "/api/admin/**",                   // 관리자 전용 API
                        "/api/products/**",                // 상품 관리
                        "/api/auctions/**"                 // 경매 관리 (공개 조회 제외)
                    ).hasRole("ADMIN")
                    
                    // 그 외 모든 요청은 인증 필요
                    .anyRequest().authenticated()
                )
                
                // 기본 로그인 폼 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                
                // HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                
                // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }

    /**
     * 비밀번호 암호화를 위한 BCrypt 인코더
     * 
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}