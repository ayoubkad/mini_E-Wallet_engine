package com.ewallet.exception;

import com.ewallet.entity.Money;

import java.util.UUID;

public class InsufficientFundsException extends BusinessException {
    public static final String ERROR_CODE =
            "INSUFFICIENT_FUNDS";

    private final UUID accountId;
    private final Money requested;
    private final Money available;

    public InsufficientFundsException(
            UUID accountId,
            Money requested,
            Money available
    ) {
        super(
                ERROR_CODE,
                "Solde insuffisant sur le compte %s"
                        .formatted(accountId)
        );

        this.accountId = accountId;
        this.requested = requested;
        this.available = available;
    }
}
