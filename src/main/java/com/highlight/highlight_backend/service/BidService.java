package com.highlight.highlight_backend.service;

import com.highlight.highlight_backend.domain.Auction;
import com.highlight.highlight_backend.domain.Bid;
import com.highlight.highlight_backend.domain.User;
import com.highlight.highlight_backend.dto.AuctionStatusResponseDto;
import com.highlight.highlight_backend.dto.BidCreateRequestDto;
import com.highlight.highlight_backend.dto.BidResponseDto;
import com.highlight.highlight_backend.dto.WinBidDetailResponseDto;
import com.highlight.highlight_backend.exception.BusinessException;
import com.highlight.highlight_backend.exception.ErrorCode;
import com.highlight.highlight_backend.repository.AuctionRepository;
import com.highlight.highlight_backend.repository.BidRepository;
import com.highlight.highlight_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 입찰 관련 비즈니스 로직 서비스
 * 
 * @author 전우선
 * @since 2025.08.15
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BidService {
    
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;
    
    /**
     * 입찰 참여
     * 
     * @param request 입찰 요청 정보
     * @param userId 입찰하는 사용자 ID
     * @return 입찰 결과 정보
     */
    @Transactional
    public BidResponseDto createBid(BidCreateRequestDto request, Long userId) {
        log.info("입찰 참여 요청: 사용자={}, 경매={}, 금액={}", userId, request.getAuctionId(), request.getBidAmount());
        
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        // 2. 경매 조회
        Auction auction = auctionRepository.findById(request.getAuctionId())
            .orElseThrow(() -> new BusinessException(ErrorCode.AUCTION_NOT_FOUND));
        
        // 3. 입찰 가능 상태 검증
        validateBidRequest(auction, request, user);
        
        // 4. 기존 최고 입찰 조회
        Optional<Bid> currentHighestBid = bidRepository.findTopByAuctionOrderByBidAmountDesc(auction);
        
        // 5. 입찰 엔티티 생성
        Bid newBid = new Bid();
        newBid.setAuction(auction);
        newBid.setUser(user);
        newBid.setBidAmount(request.getBidAmount());
        newBid.setIsAutoBid(request.getIsAutoBid() != null ? request.getIsAutoBid() : false);
        newBid.setMaxAutoBidAmount(request.getMaxAutoBidAmount());
        newBid.setStatus(Bid.BidStatus.WINNING);
        
        // 6. 기존 최고 입찰을 OUTBID로 변경 및 개인 알림
        Bid previousWinner = null;
        if (currentHighestBid.isPresent()) {
            previousWinner = currentHighestBid.get();
            previousWinner.setAsOutbid();
            bidRepository.save(previousWinner);
        }
        
        // 7. 새 입찰 저장
        Bid savedBid = bidRepository.save(newBid);
        
        // 8. 경매 정보 업데이트
        updateAuctionInfo(auction, request.getBidAmount());
        
        // 9. WebSocket으로 실시간 알림 전송
        webSocketService.sendNewBidNotification(savedBid);
        
        // 10. 이전 최고 입찰자에게 개인 알림 (다른 사용자인 경우)
        if (previousWinner != null && !previousWinner.getUser().getId().equals(userId)) {
            webSocketService.sendBidOutbidNotification(previousWinner, savedBid);
        }
        
        log.info("입찰 참여 완료: 입찰ID={}, 사용자={}, 금액={}", savedBid.getId(), userId, request.getBidAmount());
        
        return BidResponseDto.fromMyBid(savedBid);
    }
    
    /**
     * 경매 입찰 내역 조회
     * 
     * @param auctionId 경매 ID
     * @param pageable 페이징 정보
     * @return 입찰 내역 목록
     */
    public Page<BidResponseDto> getAuctionBids(Long auctionId, Pageable pageable) {
        log.info("경매 입찰 내역 조회: 경매ID={}", auctionId);
        
        Auction auction = auctionRepository.findById(auctionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.AUCTION_NOT_FOUND));
        
        Page<Bid> bids = bidRepository.findByAuctionOrderByBidAmountDesc(auction, pageable);
        
        return bids.map(BidResponseDto::from);
    }
    
    /**
     * 실시간 경매 상태 조회
     * 
     * @param auctionId 경매 ID
     * @return 경매 상태 정보
     */
    public AuctionStatusResponseDto getAuctionStatus(Long auctionId) {
        log.info("실시간 경매 상태 조회: 경매ID={}", auctionId);
        
        Auction auction = auctionRepository.findById(auctionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.AUCTION_NOT_FOUND));
        
        // 입찰 통계 조회
        Long totalBidders = bidRepository.countDistinctBiddersByAuction(auction);
        Long totalBids = bidRepository.countBidsByAuction(auction);
        
        // 현재 최고 입찰자 조회
        String winnerNickname = null;
        Optional<Bid> currentWinner = bidRepository.findTopByAuctionOrderByBidAmountDesc(auction);
        if (currentWinner.isPresent()) {
            winnerNickname = currentWinner.get().getUser().getNickname();
        }
        
        return AuctionStatusResponseDto.from(auction, totalBidders, totalBids, winnerNickname);
    }
    
    /**
     * 사용자의 입찰 내역 조회
     * 
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 사용자 입찰 내역
     */
    public Page<BidResponseDto> getUserBids(Long userId, Pageable pageable) {
        log.info("사용자 입찰 내역 조회: 사용자ID={}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        Page<Bid> bids = bidRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
        return bids.map(BidResponseDto::fromMyBid);
    }
    
    /**
     * 사용자의 낙찰 내역 조회
     * 
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 낙찰 내역
     */
    public Page<BidResponseDto> getUserWonBids(Long userId, Pageable pageable) {
        log.info("사용자 낙찰 내역 조회: 사용자ID={}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        Page<Bid> wonBids = bidRepository.findWonBidsByUser(user, pageable);
        
        return wonBids.map(BidResponseDto::fromMyBid);
    }
    
    /**
     * 낙찰 상세 정보 조회
     * 
     * @param bidId 입찰 ID
     * @param userId 사용자 ID
     * @return 낙찰 상세 정보
     */
    public WinBidDetailResponseDto getWinBidDetail(Long bidId, Long userId) {
        log.info("낙찰 상세 정보 조회: 입찰ID={}, 사용자ID={}", bidId, userId);
        
        // 1. 입찰 조회
        Bid bid = bidRepository.findById(bidId)
            .orElseThrow(() -> new BusinessException(ErrorCode.BID_NOT_FOUND));
        
        // 2. 낙찰된 입찰인지 확인
        if (bid.getStatus() != Bid.BidStatus.WON) {
            throw new BusinessException(ErrorCode.BID_NOT_FOUND);
        }
        
        // 3. 본인의 입찰인지 확인
        if (!bid.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        
        // 4. 상세 정보 반환
        return WinBidDetailResponseDto.from(bid);
    }
    
    /**
     * 입찰 요청 유효성 검증
     */
    private void validateBidRequest(Auction auction, BidCreateRequestDto request, User user) {
        // 경매 진행 중 여부 확인
        if (!auction.isInProgress()) {
            throw new BusinessException(ErrorCode.CANNOT_START_AUCTION);
        }
        
        // 시작가보다 높은지 확인
        if (request.getBidAmount().compareTo(auction.getProduct().getStartingPrice()) < 0) {
            throw new BusinessException(ErrorCode.INVALID_MINIMUM_BID);
        }
        
        // 현재 최고가보다 높은지 확인
        if (auction.getCurrentHighestBid() != null && 
            request.getBidAmount().compareTo(auction.getCurrentHighestBid()) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_MINIMUM_BID);
        }
        
        // 입찰 단위 확인
        if (!isValidBidUnit(request.getBidAmount(), auction.getBidUnit())) {
            throw new BusinessException(ErrorCode.BID_UNIT_MISMATCH);
        }
        
        // 자동 입찰인 경우 추가 검증
        if (request.getIsAutoBid() != null && request.getIsAutoBid()) {
            if (request.getMaxAutoBidAmount() == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
            if (request.getMaxAutoBidAmount().compareTo(request.getBidAmount()) < 0) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
        }
    }
    
    /**
     * 입찰 단위 유효성 검증
     */
    private boolean isValidBidUnit(BigDecimal bidAmount, BigDecimal bidUnit) {
        if (bidUnit == null || bidUnit.compareTo(BigDecimal.ZERO) <= 0) {
            return true; // 입찰 단위가 설정되지 않은 경우 통과
        }
        
        return bidAmount.remainder(bidUnit).compareTo(BigDecimal.ZERO) == 0;
    }
    
    /**
     * 경매 정보 업데이트
     */
    private void updateAuctionInfo(Auction auction, BigDecimal newBidAmount) {
        auction.setCurrentHighestBid(newBidAmount);
        auction.setTotalBids(auction.getTotalBids() + 1);
        
        // 입찰자 수 업데이트 (실제로는 더 정확한 계산 필요)
        Long totalBidders = bidRepository.countDistinctBiddersByAuction(auction);
        auction.setTotalBidders(totalBidders.intValue());
        
        auctionRepository.save(auction);
    }
}