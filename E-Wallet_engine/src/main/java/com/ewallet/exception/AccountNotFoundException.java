package com.ewallet.exception;

import java.util.UUID;

public class AccountNotFoundException extends BusinessException {
    public static final String ERROR_CODE = "ACCOUNT_NOT_FOUND";

    public AccountNotFoundException(
            UUID accountId
    ) {
        super(
                ERROR_CODE,
                "Ce compte c'est existe pas %s"
                        .formatted(accountId)
        );

    }
}