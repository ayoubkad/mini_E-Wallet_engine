package com.ewallet.entity;

import com.ewallet.exception.CurrencyMismatchException;
import jakarta.persistence.*;
import com.ewallet.enumeration.EntryType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerEntry {

    public LedgerEntry(
            EntryType entryType,
            Money amount,
            Money balanceAfter,
            Account account,
            Transaction transaction
    ) {

        validate(entryType, amount, balanceAfter, account, transaction);

        this.entryType = entryType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.account = account;
        this.transaction = transaction;
        this.createdAt = Instant.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "key", nullable = false, updatable = false)
    private UUID key;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, updatable = false)
    private EntryType entryType;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "amount",
                            nullable = false,
                            updatable = false,
                            precision = 19,
                            scale = 4
                    )
            ),
            @AttributeOverride(
                    name = "currency",
                    column = @Column(
                            name = "amount_currency",
                            nullable = false,
                            updatable = false,
                            length = 3
                    )
            )
    })
    private Money amount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "balance_after",
                            nullable = false,
                            updatable = false,
                            precision = 19,
                            scale = 4
                    )
            ),
            @AttributeOverride(
                    name = "currency",
                    column = @Column(
                            name = "balance_after_currency",
                            nullable = false,
                            updatable = false,
                            length = 3
                    )
            )
    })
    private Money balanceAfter;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false, updatable = false)
    private Transaction transaction;


    @PreUpdate
    @PreRemove
    private void protectImmutability() {
        throw new UnsupportedOperationException("LedgerEntry est immuable");
    }

    private static void validate(
            EntryType entryType,
            Money amount,
            Money balanceAfter,
            Account account,
            Transaction transaction
    ) {
        Objects.requireNonNull(entryType, "Entry type est obligatoire");
        Objects.requireNonNull(amount, "Amount est obligatoire");
        Objects.requireNonNull(balanceAfter, "Balance After Transaction est obligatoire");
        Objects.requireNonNull(account, "Account est obligatoire");
        Objects.requireNonNull(transaction, "Transaction est obligatoire");

        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException(
                    "Amount doit être positif"
            );
        }

        if (balanceAfter.isNegative()) {
            throw new IllegalArgumentException(
                    "BalanceAfter ne peut pas être négatif"
            );
        }

        if (!amount.currency().equals(account.getCurrency())) {
            throw new CurrencyMismatchException(
                    amount.currency(),
                    account.getCurrency()
            );
        }

        if (!amount.currency().equals(balanceAfter.currency())) {
            throw new CurrencyMismatchException(
                    amount.currency(),
                    balanceAfter.currency()
            );
        }

        if (!balanceAfter.equals(account.getBalance())) {
            throw new IllegalArgumentException(
                    "BalanceAfter et le solde actuel du compte doivent être égaux"
            );
        }
    }
}
