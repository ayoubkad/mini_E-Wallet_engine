package com.ewallet.repository;

import com.ewallet.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository
        extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findByReference(String reference);

    boolean existsByReference(String reference);

    Page<Transaction> findAllBySourceAccount_IdOrDestinationAccount_Id(
            UUID sourceAccountId,
            UUID destinationAccountId,
            Pageable pageable
    );
}