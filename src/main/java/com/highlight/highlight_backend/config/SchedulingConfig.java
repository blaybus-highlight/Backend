package com.highlight.highlight_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 스케줄링 및 비동기 처리 설정
 * 
 * @author 전우선
 * @since 2025.08.18
 */
@Configuration
@EnableScheduling
@EnableAsync
public class SchedulingConfig {
    // Spring의 기본 스케줄러와 비동기 처리기를 활성화
}