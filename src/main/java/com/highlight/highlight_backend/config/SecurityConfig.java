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
                
                // 세션 사용하지 않음 (JWT 사용)
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 엔드포인트 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                    // 공개 엔드포인트 (인증 불필요)
                    .requestMatchers(
                        "/api/auth/**",           // 인증 관련 API
                        "/api/public/**",         // 공개 API
                        "/swagger-ui/**",         // Swagger UI
                        "/api-docs/**",           // API 문서
                        "/v3/api-docs/**",        // OpenAPI 문서
                        "/error"                  // 에러 페이지
                    ).permitAll()
                    
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