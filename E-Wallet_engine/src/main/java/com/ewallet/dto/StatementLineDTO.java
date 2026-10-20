package com.ewallet.dto;

import com.ewallet.entity.Money;
import com.ewallet.enumeration.EntryType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record StatementLineDTO(
        EntryType entryType,
        Money amount,
        Money balanceAfter,
        Instant createdAt
) {}