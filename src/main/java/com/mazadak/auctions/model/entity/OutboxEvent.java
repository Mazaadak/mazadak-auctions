package com.mazadak.auctions.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OutboxEvent extends BaseEntity {
    private String aggregateType;

    private String eventType;

    private String payload;

    private boolean published = false;
}
