package com.highlight.highlight_backend.repository.user;

import com.highlight.highlight_backend.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAuctionRepository extends JpaRepository<Auction, Long>, JpaSpecificationExecutor<Auction> {

    @Query("SELECT a FROM Auction a WHERE a.id = :auctionId")
    Auction findOne (@Param("auctionId")Long auctionId);
}
