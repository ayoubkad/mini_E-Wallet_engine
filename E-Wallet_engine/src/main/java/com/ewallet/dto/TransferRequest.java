package com.ewallet.dto;

import com.ewallet.entity.Money;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    private UUID sourceAccount;
    private UUID destinationAccount;
    private Money amount;
    private String idempotencyKey;
}