package com.mazadak.auctions.repository;


import com.mazadak.auctions.model.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long>, JpaSpecificationExecutor<Auction> {
    @Query("""
            SELECT a FROM Auction a
            WHERE (
                (a.status IN ('SCHEDULED', 'STARTED', 'ACTIVE', 'PAUSED') AND a.endTime <= :now)
                OR (a.status = 'SCHEDULED' AND a.startTime <= :now)
            )
            """)
    List<Auction> findDueAuctions(LocalDateTime now);
}
