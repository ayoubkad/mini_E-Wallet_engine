package com.ewallet.exception;

import com.ewallet.entity.Money;

import java.util.UUID;

public class DailyLimitExceededException extends BusinessException {
    public static final String ERROR_CODE = "DAILY_LIMIT_EXCEEDED";

    private final UUID accountId;
    private final Money requested;
    private final Money available;

    public DailyLimitExceededException(
            UUID accountId,
            Money requested,
            Money available
    ) {
        super(
                ERROR_CODE,
                "daily limit exceeded %s"
                        .formatted(accountId)
        );

        this.accountId = accountId;
        this.requested = requested;
        this.available = available;
    }
}
