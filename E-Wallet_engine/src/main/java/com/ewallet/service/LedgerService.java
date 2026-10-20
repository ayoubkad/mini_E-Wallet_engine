package com.ewallet.service;

import com.ewallet.entity.Account;
import com.ewallet.entity.LedgerEntry;
import com.ewallet.entity.Money;
import com.ewallet.entity.Transaction;
import com.ewallet.enumeration.EntryType;
import com.ewallet.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LedgerService {
    private final LedgerEntryRepository ledgerEntryRepository;

    @Transactional
    public void recordDoubleEntry(Transaction transaction, Account accountDebit, Account accountCredit) {

        Objects.requireNonNull(transaction, "Transaction est obligatoire");
        Objects.requireNonNull(accountDebit, "Account debit est obligatoire");
        Objects.requireNonNull(accountCredit, "Account credit est obligatoire");


        LedgerEntry ledgerEntryDebit = new LedgerEntry(
                EntryType.DEBIT,
                transaction.getAmount(),
                accountDebit.getBalance(),
                accountDebit,
                transaction
        );

        LedgerEntry ledgerEntryCredit = new LedgerEntry(
                EntryType.CREDIT,
                transaction.getAmount(),
                accountCredit.getBalance(),
                accountCredit,
                transaction
        );

        ledgerEntryRepository.saveAll(
                List.of(ledgerEntryDebit, ledgerEntryCredit)
        );

    }

//    @Transactional
//    public Money getTotalDebits(Currency currency) {
//        Money somme = Money.zero(currency);
//        List<LedgerEntry> ledgerEntries = ledgerEntryRepository
//                .findAllByTransaction_Amount_Currency(currency);
//
//        ledgerEntries.forEach(e -> {
//            if (e.getEntryType() == EntryType.DEBIT) {
//                somme.add(e.getAmount());
//            }
//        });
//
//        return somme;
//    }

    @Transactional
    public Money getTotalDebits(Currency currency) {
        Money somme = Money.zero(currency);
        List<LedgerEntry> list = ledgerEntryRepository
                .findAllByEntryTypeAndTransaction_Amount_Currency(EntryType.DEBIT, currency);

        list.forEach(l -> somme.add(l.getAmount()));

        return somme;
    }


    @Transactional
    public Money getTotalCredits(Currency currency) {
        Money somme = Money.zero(currency);
        List<LedgerEntry> list = ledgerEntryRepository
                .findAllByEntryTypeAndTransaction_Amount_Currency(EntryType.CREDIT, currency);

        list.forEach(l -> somme.add(l.getAmount()));

        return somme;
    }
}
