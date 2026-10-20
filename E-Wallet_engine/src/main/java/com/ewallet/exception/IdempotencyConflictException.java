package com.ewallet.exception;

import com.ewallet.entity.Money;

import java.util.UUID;

public class IdempotencyConflictException extends BusinessException {
    public static final String ERROR_CODE = "IDEMPOTENCY_CONFLICT";

    private final UUID accountId;
    private final Money requested;
    private final Money available;

    public IdempotencyConflictException(
            UUID accountId,
            Money requested,
            Money available
    ) {
        super(
                ERROR_CODE,
                " idempotency conflict %s"
                        .formatted(accountId)
        );

        this.accountId = accountId;
        this.requested = requested;
        this.available = available;
    }
}
