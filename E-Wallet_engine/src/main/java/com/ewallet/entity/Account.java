package com.ewallet.entity;

import com.ewallet.enumeration.AccountStatus;
import com.ewallet.enumeration.AccountType;
import com.ewallet.exception.CurrencyMismatchException;
import com.ewallet.exception.InsufficientFundsException;
import com.ewallet.exception.InvalidAccountStateException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    public Account(
            String ownerName,
            String accountNumber,
            AccountType accountType,
            Money balance,
            Money dailyLimit

    ) {
        validate(
                ownerName,
                accountNumber,
                accountType,
                balance,
                dailyLimit
        );

        this.ownerName = ownerName.trim();
        this.accountNumber = accountNumber.trim();
        this.type = accountType;
        this.status = AccountStatus.ACTIVE;
        this.balance = balance;
        this.dailyLimit = dailyLimit;
        this.createdAt = Instant.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "account_number", nullable = false, unique = true, updatable = false)
    private String accountNumber;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "account_type", nullable = false, length = 25)
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(name = "account_status", nullable = false, length = 25)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "amount",
                    column = @Column(
                            name = "balance",
                            nullable = false,
                            precision = 19,
                            scale = 4
                    )
            ),
            @AttributeOverride(
                    name = "currency",
                    column = @Column(
                            name = "currency",
                            nullable = false,
                            length = 3
                    )
            )
    })
    private Money balance;

    @Embedded
    @AttributeOverrides(
            {@AttributeOverride(
                    name = "amount",
                    column = @Column(
                            name = "daily_limit_amount",
                            nullable = false,
                            precision = 19,
                            scale = 4
                    )
            ),
                    @AttributeOverride(
                            name = "currency",
                            column = @Column(name = "daily_limit_currency",
                                    nullable = false,
                                    length = 3
                            )
                    )}
    )
    private Money dailyLimit;

    @Version
    private Long version;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    private static void validate(
            String ownerName,
            String accountNumber,
            AccountType accountType,
            Money balance,
            Money dailyLimit
    ) {
        Objects.requireNonNull(ownerName, "Name est obligatoire");
        Objects.requireNonNull(accountNumber, "Reference est obligatoire");
        Objects.requireNonNull(accountType, "accountType est obligatoire");
        Objects.requireNonNull(balance, "balance est obligatoire");
        Objects.requireNonNull(dailyLimit, "dailyLimit est obligatoire");

        if (ownerName.isBlank()) {
            throw new IllegalArgumentException("ownerName ne peut pas être vide");
        }

        if (accountNumber.isBlank()) {
            throw new IllegalArgumentException("accountNumber ne peut pas être vide");
        }

        if (accountType != AccountType.CLIENT) {
            throw new IllegalArgumentException("Type account ne peut pas être SYSTEM");
        }
        if (balance.isNegative()) {
            throw new IllegalArgumentException("Balance ne peut pas être négatif");
        }

        if (dailyLimit.isNegativeOrZero()) {
            throw new IllegalArgumentException("DailyLimit doit être strictement positif");
        }

        if (!balance.currency().equals(dailyLimit.currency())) {
            throw new CurrencyMismatchException(
                    balance.currency(),
                    dailyLimit.currency()
            );
        }

    }

    public void debit(Money amount) {
        ensureActive();
        Objects.requireNonNull(
                amount,
                "Le montant est obligatoire"
        );
        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException(
                    "Le débit doit être strictement positif"
            );
        }
        Money after = this.balance.subtract(amount);

        if (after.isNegative()) {
            throw new InsufficientFundsException(
                    id,
                    amount,
                    this.balance
            );
        }
        this.balance = after;
    }

    public void credit(Money amount) {
        ensureActive();
        Objects.requireNonNull(amount, "Le montant est obligatoire");
        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException(
                    "Le crédit doit être strictement positif"
            );
        }
        this.balance = this.balance.add(amount);
    }

    public void ensureActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new InvalidAccountStateException(id);
        }
    }

    public Currency getCurrency() {
        return balance.currency();
    }
}
