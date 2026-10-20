package com.ewallet.entity;

import com.ewallet.enumeration.AccountType;
import com.ewallet.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void shouldDebitAccountWhenBalanceIsSufficient() {

        // Arrange

        Currency currency = Currency.getInstance("MAD");
        Account account = new Account("ayoub kaddioui", "EW-1234567890", AccountType.CLIENT,
                new Money(BigDecimal.valueOf(500), currency),
                new Money(BigDecimal.valueOf(100000), currency));

        Money debitAmount = new Money(
                BigDecimal.valueOf(500),
                currency
        );
        // Act

        account.debit(debitAmount);

        // Assert
        Currency currency1 = Currency.getInstance("MAD");

        Money expected = new Money(
                BigDecimal.valueOf(0),
                currency1
        );

        assertEquals(expected, account.getBalance());
    }

    @Test
    void shouldCreditAccountWhenBalanceIsSufficient() {

        // Arrange

        Currency currency = Currency.getInstance("MAD");
        Account account = new Account("Ahmed", "EW-0987654321", AccountType.CLIENT,
                new Money(BigDecimal.valueOf(500), currency),
                new Money(BigDecimal.valueOf(100000), currency));
        Money CreditAmount = new Money(
                BigDecimal.valueOf(100),
                currency
        );

        // Act

        account.credit(CreditAmount);

        // Assert

        Money expected = new Money(
                BigDecimal.valueOf(600),
                currency
        );

        assertEquals(expected, account.getBalance());
    }

    @Test
    void shouldRejectDebitWhenBalanceIsInsufficient() {

        // Arrange
        Currency currency = Currency.getInstance("MAD");
        Account account = new Account("Ahmed", "EW-0987654321", AccountType.CLIENT,
                new Money(BigDecimal.valueOf(500), currency),
                new Money(BigDecimal.valueOf(100000), currency));
        Money debitAmount = new Money(
                BigDecimal.valueOf(700),
                currency
        );

        Money balanceBefore = account.getBalance();

        assertThrows(
                InsufficientFundsException.class,
                () -> account.debit(debitAmount)
        );

        assertEquals(balanceBefore, account.getBalance());


    }
}