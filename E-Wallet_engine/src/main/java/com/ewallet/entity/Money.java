import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

@Embeddable
public record Money(
        @NotNull @Positive BigDecimal amount,
        @NotNull Currency currency) {

    public Money {
        Objects.requireNonNull(amount, "Le montant est obligatoire");
        Objects.requireNonNull(currency, "La devise est obligatoire");

        int digits = currency.getDefaultFractionDigits();   // EUR=2, JPY=0, KWD=3, XAU=-1
        if (digits >= 0 && amount.scale() > digits) {
            throw new IllegalArgumentException(
                    "%s : %d décimale(s) max, reçu : %s".formatted(currency.getCurrencyCode(), digits, amount));
        }
        // Normalisation du scale → equals/hashCode fiables (voir piège n°3)
        amount = (digits >= 0) ? amount.setScale(digits) : amount;
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    /** Frais, taux : l'arrondi est EXPLICITE et à la précision de la devise. */
    public Money multiply(BigDecimal factor, RoundingMode rounding) {
        int digits = Math.max(currency.getDefaultFractionDigits(), 0);
        return new Money(amount.multiply(factor).setScale(digits, rounding), currency);
    }

    public boolean isNegative()       { return amount.signum() < 0; }
    public boolean isNegativeOrZero() { return amount.signum() <= 0; }

    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount) > 0;
    }

    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency))
            throw new CurrencyMismatchException(this.currency, other.currency);
    }
}