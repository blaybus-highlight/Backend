package com.highlight.highlight_backend.repository;

import com.highlight.highlight_backend.domain.Bid;
import com.highlight.highlight_backend.domain.Auction;
import com.highlight.highlight_backend.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 입찰 정보 리포지토리
 * 
 * @author 전우선
 * @since 2025.08.15
 */
@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    
    /**
     * 특정 경매의 입찰 내역 조회 (최신순)
     */
    @Query("SELECT b FROM Bid b " +
           "WHERE b.auction = :auction " +
           "AND b.status != 'CANCELLED' " +
           "ORDER BY b.createdAt DESC")
    Page<Bid> findByAuctionOrderByCreatedAtDesc(
            @Param("auction") Auction auction, 
            Pageable pageable);
    
    /**
     * 특정 경매의 입찰 내역 조회 (입찰가 높은순)
     */
    @Query("SELECT b FROM Bid b " +
           "WHERE b.auction = :auction " +
           "AND b.status != 'CANCELLED' " +
           "ORDER BY b.bidAmount DESC, b.createdAt ASC")
    Page<Bid> findByAuctionOrderByBidAmountDesc(
            @Param("auction") Auction auction, 
            Pageable pageable);
    
    /**
     * 특정 경매의 현재 최고 입찰 조회
     */
    @Query("SELECT b FROM Bid b " +
           "WHERE b.auction = :auction " +
           "AND b.status IN ('ACTIVE', 'WINNING') " +
           "ORDER BY b.bidAmount DESC, b.createdAt ASC")
    Optional<Bid> findTopByAuctionOrderByBidAmountDesc(@Param("auction") Auction auction);
    
    /**
     * 특정 경매의 최고 입찰가 조회
     */
    @Query("SELECT MAX(b.bidAmount) FROM Bid b " +
           "WHERE b.auction = :auction " +
           "AND b.status IN ('ACTIVE', 'WINNING')")
    Optional<BigDecimal> findMaxBidAmountByAuction(@Param("auction") Auction auction);
    
    /**
     * 특정 경매의 입찰자 수 조회
     */
    @Query("SELECT COUNT(DISTINCT b.user) FROM Bid b " +
           "WHERE b.auction = :auction " +
           "AND b.status != 'CANCELLED'")
    Long countDistinctBiddersByAuction(@Param("auction") Auction auction);
    
    /**
     * 특정 경매의 총 입찰 횟수 조회
     */
    @Query("SELECT COUNT(b) FROM Bid b " +
           "WHERE b.auction = :auction " +
           "AND b.status != 'CANCELLED'")
    Long countBidsByAuction(@Param("auction") Auction auction);
    
    /**
     * 사용자의 특정 경매 입찰 내역 조회
     */
    @Query("SELECT b FROM Bid b " +
           "WHERE b.user = :user " +
           "AND b.auction = :auction " +
           "AND b.status != 'CANCELLED' " +
           "ORDER BY b.createdAt DESC")
    List<Bid> findByUserAndAuctionOrderByCreatedAtDesc(
            @Param("user") User user, 
            @Param("auction") Auction auction);
    
    /**
     * 사용자의 전체 입찰 내역 조회
     */
    @Query("SELECT b FROM Bid b " +
           "WHERE b.user = :user " +
           "AND b.status != 'CANCELLED' " +
           "ORDER BY b.createdAt DESC")
    Page<Bid> findByUserOrderByCreatedAtDesc(
            @Param("user") User user, 
            Pageable pageable);
    
    /**
     * 사용자가 낙찰받은 입찰 조회
     */
    @Query("SELECT b FROM Bid b " +
           "WHERE b.user = :user " +
           "AND b.status = 'WON' " +
           "ORDER BY b.createdAt DESC")
    Page<Bid> findWonBidsByUser(
            @Param("user") User user, 
            Pageable pageable);
    
    /**
     * 특정 사용자가 해당 경매에 입찰했는지 확인
     */
    @Query("SELECT COUNT(b) > 0 FROM Bid b " +
           "WHERE b.user = :user " +
           "AND b.auction = :auction " +
           "AND b.status != 'CANCELLED'")
    boolean existsByUserAndAuction(@Param("user") User user, @Param("auction") Auction auction);
}