-- 기본 관리자 계정 생성
-- 비밀번호: Admin123!@# (실제 서비스용 강력한 비밀번호, BCrypt 암호화됨)
INSERT INTO admin (admin_id, password, admin_name, email, role, is_active, can_manage_products, can_manage_auctions, can_manage_payments, can_manage_shipping, can_manage_auction_results, can_manage_inquiries, created_at, updated_at) 
VALUES ('admin', '$2a$12$XYZ123ABCdefGHIjklmnoPQRSTUVwxyz456789ABCDEFGHIJ123456', '시스템 관리자', 'admin@nafal.com', 'SUPER_ADMIN', true, true, true, true, true, true, true, NOW(), NOW())
ON DUPLICATE KEY UPDATE admin_id = admin_id;

-- 테스트용 일반 관리자 계정 (일부 권한만)
-- 비밀번호: Manager456!@# (실제 서비스용 강력한 비밀번호)
INSERT INTO admin (admin_id, password, admin_name, email, role, is_active, can_manage_products, can_manage_auctions, can_manage_payments, can_manage_shipping, can_manage_auction_results, can_manage_inquiries, created_at, updated_at) 
VALUES ('manager', '$2a$12$ABC789XYZdefABCijklmnoPQRSTUVwxyz123456ABCDEFGHIJ789012', '일반 관리자', 'manager@nafal.com', 'ADMIN', true, true, false, true, false, false, true, NOW(), NOW())
ON DUPLICATE KEY UPDATE admin_id = admin_id;