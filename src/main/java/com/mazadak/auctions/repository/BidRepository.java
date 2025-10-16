package com.mazadak.auctions.repository;

import com.mazadak.auctions.model.entity.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Bid> findByIdempotencyKey(String idempotencyKey);
    Page<Bid> findByAuctionId(Long auctionId, Pageable pageable);
    Page<Bid> findByAuctionIdAndBidderId(Long auctionId, Long bidderId, Pageable pageable);
    Page<Bid> findByBidderId(Long bidderId, Pageable pageable);
    Long countByAuctionId(Long auctionId);
    List<Bid> findByAuctionIdOrderByAmountDescCreatedAtAsc(Long auctionId);
}
