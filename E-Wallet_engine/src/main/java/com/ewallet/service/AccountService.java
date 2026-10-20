package com.ewallet.service;

import com.ewallet.dto.AccountResponse;
import com.ewallet.dto.StatementLineDTO;
import com.ewallet.entity.Account;
import com.ewallet.entity.LedgerEntry;
import com.ewallet.entity.Money;
import com.ewallet.enumeration.AccountType;
import com.ewallet.exception.AccountNotFoundException;
import com.ewallet.repository.AccountRepository;
import com.ewallet.repository.LedgerEntryRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ewallet.dto.OpenAccountRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountNumberGenerator accountNumberGenerator;
    private final LedgerEntryRepository ledgerEntryRepository;

    @Transactional(readOnly = true)
    public AccountResponse getAccount(UUID uuid) {
        return accountRepository.findById(uuid)
                .map(this::mapToDto)
                .orElseThrow(() -> new AccountNotFoundException(uuid));
    }

    @Transactional
    public AccountResponse openAccount(@NotNull OpenAccountRequest openAccountRequest) {


        String name = openAccountRequest.getOwnerName();
        Currency currency = openAccountRequest.getCurrency();

        Money balance = Money.zero(currency);

        String accountNumber = accountNumberGenerator.generate();

        Money dailyLimit = new Money(BigDecimal.valueOf(100000), currency);

        Account account = new Account(name, accountNumber, AccountType.CLIENT, balance, dailyLimit);
        Account saveAccount = accountRepository.save(account);

        return mapToDto(saveAccount);
    }

    @Transactional(readOnly = true)
    public Page<StatementLineDTO> getStatement(UUID id, Pageable pageable) {

        Account account = accountRepository.findById(id).orElseThrow(() ->
                new AccountNotFoundException(id)
        );

        return ledgerEntryRepository
                .findAllByAccount_IdOrderByCreatedAtDesc(account.getId(), pageable)
                .map(this::mapToStatementLine);
    }

    private AccountResponse mapToDto(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .ownerName(account.getOwnerName())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .status(account.getStatus())
                .build();
    }

    private StatementLineDTO mapToStatementLine(LedgerEntry ledgerEntry){
        return StatementLineDTO.builder()
                .entryType(ledgerEntry.getEntryType())
                .amount(ledgerEntry.getAmount())
                .balanceAfter(ledgerEntry.getBalanceAfter())
                .createdAt(ledgerEntry.getCreatedAt())
                .build();
    };
}
