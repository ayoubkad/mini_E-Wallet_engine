package com.ewallet.entity;

import com.ewallet.enumeration.TransactionStatus;
import com.ewallet.enumeration.TransactionType;
import com.ewallet.exception.CurrencyMismatchException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {

    public Transaction(String reference, TransactionType type, Account sourceAccount, Account destinationAccount, Money amount, Money fee) {
        validate(
                reference,
                type,
                sourceAccount,
                destinationAccount,
                amount,
                fee
        );

        this.reference = reference.trim();
        this.type = type;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.fee = fee;
        this.status = TransactionStatus.PENDING;
        this.createdAt = Instant.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 15, updatable = false)
    private String reference;

    @Column(nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "amount",
                            nullable = false,
                            precision = 19,
                            scale = 4
                    )
            ),
            @AttributeOverride(
                    name = "currency",
                    column = @Column(
                            name = "amount_currency",
                            nullable = false,
                            length = 3
                    )
            )
    })
    private Money amount;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "fee_amount",
                            nullable = false,
                            precision = 19,
                            scale = 4
                    )
            ),
            @AttributeOverride(
                    name = "currency",
                    column = @Column(
                            name = "fee_currency",
                            nullable = false,
                            length = 3
                    )
            )
    })
    private Money fee;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @JoinColumn(name = "source_account_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Account sourceAccount;

    @JoinColumn(name = "destination_account_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Account destinationAccount;

    @Version
    private Long version;

    private static void validate(
            String reference,
            TransactionType type,
            Account sourceAccount,
            Account destinationAccount,
            Money amount,
            Money fee
    ) {
        Objects.requireNonNull(reference, "Reference est obligatoire");
        Objects.requireNonNull(type, "Type est obligatoire");
        Objects.requireNonNull(sourceAccount, "source Account est obligatoire");
        Objects.requireNonNull(destinationAccount, "destination est obligatoire");
        Objects.requireNonNull(amount, "Amount est obligatoire");
        Objects.requireNonNull(fee, "Fee est obligatoire");

        if (reference.isBlank()) {
            throw new IllegalArgumentException("La référence ne peut pas être vide");
        }

        if(reference.length() < 50){
            throw new IllegalArgumentException("La référence ne peut pas dépasser 50 caractères");
        }

        if (amount.isNegativeOrZero()) {
            throw new IllegalArgumentException("Amount doit être positive");
        }

        if (fee.isNegative()) {
            throw new IllegalArgumentException("Fee doit être positive ou 0");
        }

        if (!amount.currency().equals(fee.currency())) {
            throw new CurrencyMismatchException(
                    amount.currency(),
                    fee.currency()
            );
        }

        if (!amount.currency().equals(sourceAccount.getCurrency())) {
            throw new CurrencyMismatchException(
                    amount.currency(),
                    sourceAccount.getCurrency()
            );
        }

        if (!amount.currency().equals(destinationAccount.getCurrency())) {
            throw new CurrencyMismatchException(
                    amount.currency(),
                    destinationAccount.getCurrency()
            );
        }

        if (sourceAccount.getId() == null) {
            throw new IllegalArgumentException(
                    "Le compte source doit être persisté"
            );
        }

        if (destinationAccount.getId() == null) {
            throw new IllegalArgumentException(
                    "Le compte destination doit être persisté"
            );
        }

        if (sourceAccount.getId().equals(destinationAccount.getId())) {
            throw new IllegalArgumentException(
                    "Le compte source et destination doivent être différents"
            );
        }
    }

}
