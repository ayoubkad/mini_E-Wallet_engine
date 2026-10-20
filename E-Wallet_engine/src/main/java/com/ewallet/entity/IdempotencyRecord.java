package com.ewallet.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "idempotency_records")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class IdempotencyRecord {

    public IdempotencyRecord(String key, String requestHash, UUID transactionId) {
        validate(key, requestHash, transactionId);

        this.key = key.trim();
        this.requestHash = requestHash.trim();
        this.transactionId = transactionId;
        this.createdAt = Instant.now();
        this.expiresAt = null;
    }

    @Id
    @Column(name = "idempotency_key", nullable = false, updatable = false, length = 128)
    private String key;

    @Column(name = "request_hash", nullable = false, updatable = false, length = 64)
    private String requestHash;

    @Column(name = "transaction_id", nullable = false, updatable = false)
    private UUID transactionId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    private static void validate(String key, String requestHash, UUID transactionId) {
        Objects.requireNonNull(key, "Key est obligatoire");
        Objects.requireNonNull(requestHash, "Request Hash est obligatoire");
        Objects.requireNonNull(transactionId, "transaction ID est obligatoire");

        if (key.trim().isBlank()) {
            throw new IllegalArgumentException("Key ne peut pas être vide");
        }
        if (requestHash.trim().isBlank()) {
            throw new IllegalArgumentException("Request Hash ne peut pas être vide");
        }
        if (key.trim().length() > 128) {
            throw new IllegalArgumentException("Key ne peut pas dépasser 128 caractères");
        }
        if (!requestHash.trim().matches("[0-9a-fA-F]{64}")) {
            throw new IllegalArgumentException("Le hash SHA-256 doit contenir exactement 64 caractères hexadécimaux");
        }
    }
}
