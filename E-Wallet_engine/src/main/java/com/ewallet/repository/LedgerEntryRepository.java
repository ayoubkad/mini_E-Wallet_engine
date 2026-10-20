package com.ewallet.repository;

import com.ewallet.entity.LedgerEntry;
import com.ewallet.enumeration.EntryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Currency;
import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {

    Page<LedgerEntry> findAllByAccount_IdOrderByCreatedAtDesc(
            UUID accountId,
            Pageable pageable
    );

    List<LedgerEntry> findAllByTransaction_Id(
            UUID transactionId
    );

    List<LedgerEntry> findAllByTransaction_Amount_Currency(Currency transactionAmountCurrency);

    List<LedgerEntry> findAllByEntryTypeAndTransaction_Amount_Currency(EntryType entryType, Currency transactionAmountCurrency);

    List<LedgerEntry> findAllByKey(UUID key);

    long countByAccount_Id(UUID accountId);

    long countByTransaction_Id(UUID transactionId);
}