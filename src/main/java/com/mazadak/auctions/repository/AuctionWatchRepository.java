package com.mazadak.auctions.repository;

import com.mazadak.auctions.model.entity.AuctionWatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuctionWatchRepository extends JpaRepository<AuctionWatch, Long> {
    Optional<AuctionWatch> findAuctionWatchByUserIdAndAuction_Id(Long userId, Long auctionId);

    List<Long> findAllByAuction_Id(Long auctionId);
}
