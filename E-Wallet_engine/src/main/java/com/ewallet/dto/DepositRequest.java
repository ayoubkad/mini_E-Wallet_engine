package com.ewallet.dto;

import com.ewallet.entity.Money;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequest {

    private UUID accountId;
    private Money amount;
    private String idempotencyKey;
}