package com.highlight.highlight_backend.repository.spec;

import com.highlight.highlight_backend.domain.Auction;
import com.highlight.highlight_backend.domain.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;

public class AuctionSpecs {

    // 카테고리 필터
    public static Specification<Auction> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(category)) {
                return null; // 조건 값이 없으면 무시
            }
            Join<Auction, Product> productJoin = root.join("product"); // Auction -> Product 조인
            return criteriaBuilder.equal(productJoin.get("category"), category);
        };
    }

    // 브랜드 필터
    public static Specification<Auction> hasBrand(String brand) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(brand)) {
                return null;
            }
            Join<Auction, Product> productJoin = root.join("product");
            return criteriaBuilder.equal(productJoin.get("brandName"), brand);
        };
    }

    // 행사명 필터 (Auction의 title에 포함되는지 검색)
    public static Specification<Auction> hasEventName(String eventName) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(eventName)) {
                return null;
            }
            return criteriaBuilder.like(root.get("auctionTitle"), "%" + eventName + "%");
        };
    }

    // 가격 범위 필터
    public static Specification<Auction> betweenPrice(Long minPrice, Long maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return null;
            }
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("currentHighestBid"), minPrice, maxPrice);
            }
            if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("currentHighestBid"), minPrice);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("currentHighestBid"), maxPrice);
        };
    }


}
