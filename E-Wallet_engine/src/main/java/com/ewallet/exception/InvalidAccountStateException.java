package com.ewallet.exception;

import java.util.UUID;

public class InvalidAccountStateException extends BusinessException {
    public static final String ERROR_CODE = "INVALID_ACCOUNT_STATE";

    private final UUID accountId;

    public InvalidAccountStateException(UUID accountId) {
        super(ERROR_CODE, "invalid account statu %s".formatted(accountId)
        );

        this.accountId = accountId;
    }
}
