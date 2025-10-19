package com.mazadak.auctions.repository;

import com.mazadak.auctions.dto.response.BidResponse;
import com.mazadak.auctions.model.entity.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = """
       SELECT b.*
       FROM bids b
       INNER JOIN (
           SELECT bidder_id, MAX(amount) AS max_amount
           FROM bids
           WHERE auction_id = :auctionId
           GROUP BY bidder_id
       ) AS sub ON b.bidder_id = sub.bidder_id
           AND b.amount = sub.max_amount
       WHERE b.auction_id = :auctionId
       ORDER BY b.amount DESC, b.created_at ASC
    """, nativeQuery = true)
    List<Bid> findHighestBidsPerBidderByAuctionId(@Param("auctionId") UUID auctionId);
}
