package com.mazadak.auctions.repository;

import com.mazadak.auctions.model.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Query("""
        SELECT obe from OutboxEvent obe
        WHERE obe.published = false
    """)
    List<OutboxEvent> findByPublishedFalse();
}
