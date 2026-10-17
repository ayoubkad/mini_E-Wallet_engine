package com.ewallet.exception;

public class CurrencyMismatchException extends RuntimeException {
  public CurrencyMismatchException(String message) {
    super(message);
  }
}
