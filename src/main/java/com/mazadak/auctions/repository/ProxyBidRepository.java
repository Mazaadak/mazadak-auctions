package com.mazadak.auctions.repository;

import com.mazadak.auctions.model.entity.ProxyBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProxyBidRepository extends JpaRepository<ProxyBid, Long> {
    Optional<ProxyBid> findByAuctionIdAndBidderId(Long auctionId, Long BidderId);
    void deleteByAuctionIdAndBidderId(Long auctionId, Long bidderId);
}
