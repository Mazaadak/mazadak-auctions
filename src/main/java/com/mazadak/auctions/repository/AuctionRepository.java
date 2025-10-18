package com.mazadak.auctions.repository;


import com.mazadak.auctions.model.entity.Auction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface AuctionRepository extends JpaRepository<Auction, UUID>, JpaSpecificationExecutor<Auction> {
  
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT a FROM Auction a WHERE a.id = :id")
    Optional<Auction> findByIdForUpdate(UUID id);
  
    @Query("""
            SELECT a FROM Auction a
            WHERE (
                (a.status IN ('SCHEDULED', 'STARTED', 'ACTIVE', 'PAUSED') AND a.endTime <= :now)
                OR (a.status = 'SCHEDULED' AND a.startTime <= :now)
            )
            """)
    List<Auction> findDueAuctions(LocalDateTime now);


}
