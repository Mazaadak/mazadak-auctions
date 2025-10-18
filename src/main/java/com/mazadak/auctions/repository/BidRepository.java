package com.mazadak.auctions.repository;

import com.mazadak.auctions.model.entity.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {
    Optional<Bid> findByIdempotencyKey(String idempotencyKey);
    Page<Bid> findByAuctionId(UUID auctionId, Pageable pageable);
    Page<Bid> findByAuctionIdAndBidderId(UUID auctionId, UUID bidderId, Pageable pageable);
    Page<Bid> findByBidderId(UUID bidderId, Pageable pageable);
    Long countByAuctionId(UUID auctionId);
    List<Bid> findByAuctionIdOrderByAmountDescCreatedAtAsc(UUID auctionId);
}
