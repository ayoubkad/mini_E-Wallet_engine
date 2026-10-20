package com.ewallet.dto;

import com.ewallet.entity.Money;
import com.ewallet.enumeration.TransactionStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private UUID transactionId;
    private TransactionStatus status;
    private Money amount;
    private Money fee;
    private Instant executedAt;
}
