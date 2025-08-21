-- 사용자 등급 컬럼 추가
-- MySQL에서 'rank'는 예약어이므로 'user_rank'로 컬럼명 지정

ALTER TABLE user ADD COLUMN user_rank ENUM('SEED', 'Leaflet', 'Trunker', 'Flower') NOT NULL DEFAULT 'SEED';
ALTER TABLE user ADD COLUMN participation_count BIGINT NOT NULL DEFAULT 0;

-- 기존 사용자들의 참여 횟수 초기화 (필요한 경우)
-- UPDATE user SET participation_count = 0 WHERE participation_count IS NULL;
