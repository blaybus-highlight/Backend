-- 기본 관리자 계정 생성
-- 비밀번호: admin123 (BCrypt 암호화됨)
INSERT INTO admin (admin_id, password, admin_name, email, role, is_active, created_at, updated_at) 
VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '시스템 관리자', 'admin@nafal.com', 'SUPER_ADMIN', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE admin_id = admin_id;

-- 테스트용 일반 관리자 계정
INSERT INTO admin (admin_id, password, admin_name, email, role, is_active, created_at, updated_at) 
VALUES ('manager', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '일반 관리자', 'manager@nafal.com', 'ADMIN', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE admin_id = admin_id;