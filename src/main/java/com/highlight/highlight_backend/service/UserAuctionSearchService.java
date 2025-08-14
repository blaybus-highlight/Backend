package com.highlight.highlight_backend.service;

import com.highlight.highlight_backend.domain.Auction;
import com.highlight.highlight_backend.dto.UserAuctionResponseDto;
import com.highlight.highlight_backend.repository.user.UserAuctionRepository;
import com.highlight.highlight_backend.repository.spec.AuctionSpecs; // import 추가
import lombok.RequiredArgsConstructor; // AllArgsConstructor 대신 사용
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification; // import 추가
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 일반 User에게 필터링, 정렬조건을 받고 정렬된 값을 찾는 Service
 *
 *
 * @author 탁찬홍
 * @since 2025.08.14
 */
@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 만들어줍니다.
public class UserAuctionSearchService {

    private final UserAuctionRepository userAuctionRepository;

    public Page<UserAuctionResponseDto> getProductsFiltered(
            String category, Long minPrice, Long maxPrice, String brand, String eventName,
            String sortCode, Pageable pageable) {

        // 1. Specification 조합 -> Where 문을 동적으로 만듦
        Specification<Auction> spec = Specification.where(null);

        if (StringUtils.hasText(category)) {
            spec = spec.and(AuctionSpecs.hasCategory(category));
        }
        if (StringUtils.hasText(brand)) {
            spec = spec.and(AuctionSpecs.hasBrand(brand));
        }
        if (StringUtils.hasText(eventName)) {
            spec = spec.and(AuctionSpecs.hasEventName(eventName));
        }
        if (minPrice != null || maxPrice != null) {
            spec = spec.and(AuctionSpecs.betweenPrice(minPrice, maxPrice));
        }

        // 2. 정렬(Sort) 조건 적용
        Sort sort = getSort(sortCode);
        Pageable newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // 3. Repository 호출
        Page<Auction> auctionPage = userAuctionRepository.findAll(spec, newPageable);

        // 4. DTO로 변환하여 반환
        return auctionPage.map(UserAuctionResponseDto::from);
    }

    private Sort getSort(String sortCode) {
        if (!StringUtils.hasText(sortCode)) {
            return Sort.by(Sort.Direction.DESC, "createdAt"); // 기본 정렬: 최신순
        }

        switch (sortCode.toLowerCase()) {
            case "ending": // 마감임박순
                return Sort.by(Sort.Direction.ASC, "endTime");
            case "popular": // 인기순 (예: 입찰 수 기준)
                return Sort.by(Sort.Direction.DESC, "bidCount");
            case "newest": // 신규순
            default:
                return Sort.by(Sort.Direction.DESC, "createdAt");
        }
    }
}