package com.highlight.highlight_backend.repository.user;

import com.highlight.highlight_backend.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserAuctionRepository extends JpaRepository<Auction, Long>, JpaSpecificationExecutor<Auction> {

}
