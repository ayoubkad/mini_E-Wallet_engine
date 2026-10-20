package com.ewallet.exception;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {



    protected BusinessException(String errorCode, String message) {
        super(message);
    }
}