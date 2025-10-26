package com.mazadak.auctions.repository;

import com.mazadak.auctions.model.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord, UUID> {
}
