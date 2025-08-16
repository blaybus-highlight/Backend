package com.highlight.highlight_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(buildApiInfo())
                .addSecurityItem(buildSecurityRequirement())
                .components(buildComponents())
                .servers(buildServers());
    }
    
    /**
     * API 기본 정보 구성
     */
    private Info buildApiInfo() {
        return new Info()
                .title("🚀 Highlight Backend API")
                .description(buildApiDescription())
                .version("v1.0.0")
                .contact(buildContactInfo())
                .license(buildLicenseInfo());
    }
    
    /**
     * API 상세 설명 구성
     */
    private String buildApiDescription() {
        return """
                ## 📋 블레이버스 해커톤 2025
                
                **Highlight 팀의 MVP 백엔드 API 서비스입니다.**
                
                ### 🚨 인증 방법
                1. `/api/auth/login` 엔드포인트로 로그인
                2. 응답으로 받은 JWT 토큰을 복사
                3. 우측 상단 🔒 **Authorize** 버튼 클릭
                4. `Bearer {토큰}` 형식으로 입력
                
                ### 📞 문의사항
                개발 중 이슈나 질문이 있으시면 언제든 연락주세요!
                """;
    }
    
    /**
     * 연락처 정보 구성
     */
    private Contact buildContactInfo() {
        return new Contact()
                .name("Highlight Backend Team (전우선, 탁찬홍)")
                .email("wooxexn@gmail.com")
                .url("https://github.com/blaybus-highlight");
    }
    
    /**
     * 라이센스 정보 구성
     */
    private License buildLicenseInfo() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }
    
    /**
     * 보안 요구사항 구성
     */
    private SecurityRequirement buildSecurityRequirement() {
        return new SecurityRequirement().addList("bearerAuth");
    }
    
    /**
     * API 컴포넌트 구성 (보안 스키마 등)
     */
    private Components buildComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", buildJwtSecurityScheme());
    }
    
    /**
     * JWT 보안 스키마 구성
     */
    private SecurityScheme buildJwtSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT 토큰을 입력하세요. Bearer 접두사는 자동으로 추가됩니다.");
    }
    
    /**
     * API 서버 정보 구성
     */
    private List<Server> buildServers() {
        return Arrays.asList(
                new Server()
                        .url("http://localhost:8085")
                        .description("🔧 Local Development Server"),
                new Server()
                        .url("http://ec2-43-201-71-156.ap-northeast-2.compute.amazonaws.com:8085")
                        .description("🚀 Production Server (배포 후 활성화)")
        );
    }
}