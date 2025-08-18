package com.highlight.highlight_backend.repository;

import com.highlight.highlight_backend.domain.UserProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 상품 조회 이력 Repository
 * 
 * @author 전우선
 * @since 2025.08.18
 */
@Repository
public interface UserProductViewRepository extends JpaRepository<UserProductView, Long> {

    /**
     * 특정 기간 내 사용자/세션의 상품 조회 이력 조회
     * 
     * @param userId 사용자 ID (null 가능)
     * @param sessionId 세션 ID (null 가능)
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @return 조회 이력 목록
     */
    @Query("SELECT upv FROM UserProductView upv " +
           "WHERE (upv.userId = :userId OR (upv.userId IS NULL AND upv.sessionId = :sessionId)) " +
           "AND upv.viewedAt BETWEEN :startTime AND :endTime " +
           "ORDER BY upv.viewedAt DESC")
    List<UserProductView> findByUserOrSessionAndViewedAtBetween(
            @Param("userId") Long userId,
            @Param("sessionId") String sessionId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 특정 상품과 함께 조회된 다른 상품들 조회 (동일 세션 기준)
     * 최근 30일 기준으로 조회
     * 
     * @param productId 기준 상품 ID
     * @param days 조회 기간 (일)
     * @return 함께 조회된 상품 ID와 횟수
     */
    @Query("SELECT upv2.product.id as productId, COUNT(*) as coViewCount " +
           "FROM UserProductView upv1 " +
           "JOIN UserProductView upv2 ON upv1.sessionId = upv2.sessionId " +
           "WHERE upv1.product.id = :productId " +
           "AND upv2.product.id != :productId " +
           "AND upv1.viewedAt >= :sinceDate " +
           "AND upv2.viewedAt >= :sinceDate " +
           "AND ABS(FUNCTION('TIMESTAMPDIFF', MINUTE, upv1.viewedAt, upv2.viewedAt)) <= 60 " +
           "GROUP BY upv2.product.id " +
           "HAVING COUNT(*) >= 2 " +
           "ORDER BY COUNT(*) DESC")
    List<Object[]> findCoViewedProductsBySession(
            @Param("productId") Long productId,
            @Param("sinceDate") LocalDateTime sinceDate);

    /**
     * 특정 상품과 함께 조회된 다른 상품들 조회 (동일 사용자 기준)
     * 
     * @param productId 기준 상품 ID  
     * @param days 조회 기간 (일)
     * @return 함께 조회된 상품 ID와 횟수
     */
    @Query("SELECT upv2.product.id as productId, COUNT(*) as coViewCount " +
           "FROM UserProductView upv1 " +
           "JOIN UserProductView upv2 ON upv1.userId = upv2.userId " +
           "WHERE upv1.product.id = :productId " +
           "AND upv2.product.id != :productId " +
           "AND upv1.userId IS NOT NULL " +
           "AND upv1.viewedAt >= :sinceDate " +
           "AND upv2.viewedAt >= :sinceDate " +
           "AND ABS(FUNCTION('TIMESTAMPDIFF', HOUR, upv1.viewedAt, upv2.viewedAt)) <= 24 " +
           "GROUP BY upv2.product.id " +
           "HAVING COUNT(*) >= 1 " +
           "ORDER BY COUNT(*) DESC")
    List<Object[]> findCoViewedProductsByUser(
            @Param("productId") Long productId,
            @Param("sinceDate") LocalDateTime sinceDate);

    /**
     * 사용자/세션의 최근 상품 조회 기록에서 중복 제거
     * 동일 상품을 1시간 이내에 여러 번 조회한 경우 가장 최근 기록만 반환
     * 
     * @param userId 사용자 ID
     * @param sessionId 세션 ID  
     * @param hours 중복 제거 기준 시간 (시간)
     * @return 중복 제거된 조회 이력
     */
    @Query("SELECT upv FROM UserProductView upv " +
           "WHERE (upv.userId = :userId OR (upv.userId IS NULL AND upv.sessionId = :sessionId)) " +
           "AND upv.viewedAt >= :sinceTime " +
           "AND upv.id IN (" +
           "    SELECT MAX(upv2.id) " +
           "    FROM UserProductView upv2 " +
           "    WHERE (upv2.userId = :userId OR (upv2.userId IS NULL AND upv2.sessionId = :sessionId)) " +
           "    AND upv2.viewedAt >= :sinceTime " +
           "    GROUP BY upv2.product.id" +
           ") " +
           "ORDER BY upv.viewedAt DESC")
    List<UserProductView> findRecentUniqueViewsByUserOrSession(
            @Param("userId") Long userId,
            @Param("sessionId") String sessionId,
            @Param("sinceTime") LocalDateTime sinceTime);

    /**
     * 특정 사용자/세션의 특정 상품에 대한 최근 조회 기록 조회
     * 중복 방지용
     * 
     * @param userId 사용자 ID
     * @param sessionId 세션 ID
     * @param productId 상품 ID
     * @param sinceTime 조회 시작 시간
     * @return 최근 조회 기록
     */
    @Query("SELECT upv FROM UserProductView upv " +
           "WHERE (upv.userId = :userId OR (upv.userId IS NULL AND upv.sessionId = :sessionId)) " +
           "AND upv.product.id = :productId " +
           "AND upv.viewedAt >= :sinceTime " +
           "ORDER BY upv.viewedAt DESC")
    Optional<UserProductView> findRecentViewByUserOrSessionAndProduct(
            @Param("userId") Long userId,
            @Param("sessionId") String sessionId,
            @Param("productId") Long productId,
            @Param("sinceTime") LocalDateTime sinceTime);

    /**
     * 특정 기간 동안 가장 많이 조회된 상품 목록
     * 
     * @param sinceDate 조회 시작 날짜
     * @param limit 결과 개수 제한
     * @return 상품 ID와 조회수 목록
     */
    @Query("SELECT upv.product.id as productId, COUNT(*) as viewCount " +
           "FROM UserProductView upv " +
           "WHERE upv.viewedAt >= :sinceDate " +
           "GROUP BY upv.product.id " +
           "ORDER BY COUNT(*) DESC")
    List<Object[]> findMostViewedProducts(@Param("sinceDate") LocalDateTime sinceDate, 
                                         @Param("limit") Integer limit);
}