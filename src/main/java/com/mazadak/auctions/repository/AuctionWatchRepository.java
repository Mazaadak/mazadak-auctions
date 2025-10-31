package com.mazadak.auctions.repository;

import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.entity.AuctionWatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuctionWatchRepository extends JpaRepository<AuctionWatch, UUID> {
    Optional<AuctionWatch> findAuctionWatchByUserIdAndAuction_Id(UUID userId, UUID auctionId);

    List<AuctionWatch> findAllByAuction_Id(UUID auctionId);

    Boolean existsByUserIdAndId(UUID userId, UUID id);

    List<AuctionWatch> findAllByUserId(UUID userId);
}
