package com.ewallet.entity;


import com.ewallet.exception.CurrencyMismatchException;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;


@Embeddable
public record Money(
        @NotNull BigDecimal amount,
        @NotNull Currency currency
) {

    public Money {
        Objects.requireNonNull(amount, "Le montant est obligatoire");
        Objects.requireNonNull(currency, "La devise est obligatoire");

        int digits = currency.getDefaultFractionDigits();

        amount = amount.setScale(
                digits,
                RoundingMode.HALF_EVEN
        );

        if (digits >= 0 && amount.scale() > digits) {
            throw new IllegalArgumentException("%s : %d décimale(s) max, reçu : %s".formatted(currency.getCurrencyCode(), digits, amount));
        }

        amount = digits >= 0 ? amount.setScale(digits, RoundingMode.CEILING) : amount;
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money money) {
        requireSameCurrency(money);
        return new Money(this.amount.add(money.amount), currency);
    }

    public Money subtract(Money money) {
        requireSameCurrency(money);
        return new Money(this.amount.subtract(money.amount), currency);
    }

    public Money multiply(BigDecimal factor, RoundingMode roundingMode) {
        Objects.requireNonNull(factor, "Le facteur est obligatoire");
        Objects.requireNonNull(roundingMode, "Le mode d'arrondi est obligatoire");
        int digits = Math.max(currency.getDefaultFractionDigits(), 0);

        return new Money(
                amount.multiply(factor).setScale(digits, roundingMode),
                currency
        );
    }

    public boolean isNegative() {
        return amount.signum() < 0;
    }

    public boolean isNegativeOrZero() {
        return amount.signum() <= 0;
    }

    public boolean isGreaterThan(Money money) {
        requireSameCurrency(money);
        return this.amount.compareTo(money.amount) > 0;
    }

    private void requireSameCurrency(Money money) {
        Objects.requireNonNull(money, "Le montant Money est obligatoire");
        if (!this.currency.equals(money.currency)) {
            throw new CurrencyMismatchException(this.currency, money.currency);
        }
    }
}