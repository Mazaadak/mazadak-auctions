package com.mazadak.auctions.repository;

import com.mazadak.auctions.model.entity.ProxyBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProxyBidRepository extends JpaRepository<ProxyBid, UUID> {
    Optional<ProxyBid> findByAuctionIdAndBidderId(UUID auctionId, UUID BidderId);
    void deleteByAuctionIdAndBidderId(UUID auctionId, UUID bidderId);
}
