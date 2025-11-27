package com.mazadak.auctions.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "idempotency_records")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IdempotencyRecord {
    @Id
    @Column(name = "idempotency_key", nullable = false, unique = true)
    private UUID idempotencyKey;
    private boolean processed;
}
