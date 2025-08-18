-- 사용자 행동 기반 추천 시스템을 위한 테이블 생성 스크립트
-- 작성자: 전우선
-- 작성일: 2025.08.18

-- 1. 사용자 상품 조회 이력 테이블
CREATE TABLE IF NOT EXISTS user_product_views (
    view_id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NULL COMMENT '사용자 ID (비회원은 NULL)',
    session_id VARCHAR(255) NULL COMMENT '세션 ID',
    product_id BIGINT NOT NULL COMMENT '조회한 상품 ID',
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '조회 시간',
    ip_address VARCHAR(45) NULL COMMENT 'IP 주소',
    user_agent TEXT NULL COMMENT 'User-Agent 정보',
    duration_seconds INT NULL COMMENT '조회 지속 시간(초)',
    
    PRIMARY KEY (view_id),
    INDEX idx_user_product_time (user_id, product_id, viewed_at),
    INDEX idx_session_product_time (session_id, product_id, viewed_at),
    INDEX idx_product_viewed_at (product_id, viewed_at),
    INDEX idx_viewed_at (viewed_at),
    
    CONSTRAINT fk_user_product_views_product 
        FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='사용자 상품 조회 이력';

-- 2. 상품 연관도 테이블  
CREATE TABLE IF NOT EXISTS product_associations (
    association_id BIGINT NOT NULL AUTO_INCREMENT,
    source_product_id BIGINT NOT NULL COMMENT '기준 상품 ID',
    target_product_id BIGINT NOT NULL COMMENT '연관 상품 ID',
    association_score DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '연관도 점수 (0.00~100.00)',
    co_view_count INT NOT NULL DEFAULT 0 COMMENT '함께 조회된 총 횟수',
    same_session_count INT NOT NULL DEFAULT 0 COMMENT '동일 세션에서 함께 조회된 횟수',
    same_user_count INT NOT NULL DEFAULT 0 COMMENT '동일 사용자가 함께 조회한 횟수',
    last_co_viewed_at TIMESTAMP NULL COMMENT '마지막 함께 조회된 시간',
    score_calculated_at TIMESTAMP NULL COMMENT '연관도 점수 계산 시간',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    
    PRIMARY KEY (association_id),
    UNIQUE KEY uk_source_target (source_product_id, target_product_id),
    INDEX idx_source_product_score (source_product_id, association_score DESC),
    INDEX idx_target_product_score (target_product_id, association_score DESC),
    INDEX idx_score_calculated_at (score_calculated_at),
    INDEX idx_last_co_viewed_at (last_co_viewed_at),
    
    CONSTRAINT fk_product_associations_source 
        FOREIGN KEY (source_product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    CONSTRAINT fk_product_associations_target 
        FOREIGN KEY (target_product_id) REFERENCES products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='상품 연관도 정보';

-- 3. 인덱스 추가 최적화 (대용량 데이터 대비)
-- 조회 이력 테이블의 파티셔닝을 위한 준비 (선택사항)
-- ALTER TABLE user_product_views 
-- PARTITION BY RANGE (YEAR(viewed_at)) (
--     PARTITION p2025 VALUES LESS THAN (2026),
--     PARTITION p2026 VALUES LESS THAN (2027),
--     PARTITION p_future VALUES LESS THAN MAXVALUE
-- );

-- 4. 테이블 통계 정보
INSERT INTO INFORMATION_SCHEMA.TABLES 
SELECT 'user_product_views 테이블: 사용자 상품 조회 이력을 저장하여 행동 분석의 기초 데이터로 활용' as comment
WHERE NOT EXISTS (SELECT 1 FROM user_product_views LIMIT 1);

INSERT INTO INFORMATION_SCHEMA.TABLES 
SELECT 'product_associations 테이블: 상품 간 연관도 점수를 저장하여 함께 본 상품 추천에 활용' as comment  
WHERE NOT EXISTS (SELECT 1 FROM product_associations LIMIT 1);