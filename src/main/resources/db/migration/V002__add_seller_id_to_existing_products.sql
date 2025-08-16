-- 기존 상품들에 sellerId 설정 (NAFAL = 1)
-- 모든 기존 상품의 sellerId를 1로 설정
UPDATE product SET seller_id = 1 WHERE seller_id IS NULL;