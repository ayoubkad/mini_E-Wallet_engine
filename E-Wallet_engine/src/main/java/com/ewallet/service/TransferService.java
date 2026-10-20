package com.ewallet.service;

import com.ewallet.dto.DepositRequest;
import com.ewallet.entity.Account;
import com.ewallet.entity.IdempotencyRecord;
import com.ewallet.entity.Money;
import com.ewallet.entity.Transaction;
import com.ewallet.enumeration.AccountType;
import com.ewallet.enumeration.TransactionType;
import com.ewallet.exception.AccountNotFoundException;
import com.ewallet.exception.IdempotencyConflictException;
import com.ewallet.repository.AccountRepository;
import com.ewallet.repository.IdempotencyRecordRepository;
import com.ewallet.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final TransferNumberGenerator transferNumberGenerator;
    private final PercentageFeePolicy percentageFeePolicy;

    @Transactional
    public Transaction deposit(DepositRequest depositRequest) {

        Account accountSystem = new Account("System",
                "TXN-0000000000",
                AccountType.SYSTEM, depositRequest.getAmount(),
                new Money(BigDecimal.valueOf(10000),
                        depositRequest.getAmount().currency())
        );

        UUID id = depositRequest.getAccountId();
        String idempotencyKey = depositRequest.getIdempotencyKey();
        IdempotencyRecord idempotencyRecord = idempotencyRecordRepository.findByKey(idempotencyKey)
                .orElseThrow(() -> new RuntimeException("idempotency  n'est existe pas"));
        String hash = DigestUtils.sha256Hex(String.valueOf(depositRequest));
        UUID idempotencyRecordTransactionId = idempotencyRecord.getTransactionId();
        Transaction transactionOriginale = transactionRepository.findById(idempotencyRecordTransactionId)
                .orElseThrow(() -> new RuntimeException("Transaction n'est existe pas"));
        Account account = accountRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new AccountNotFoundException(id));


        if (idempotencyRecordRepository.existsByKey(idempotencyKey)) {

            if (!idempotencyRecord.getRequestHash().equals(hash)) {
                throw new IdempotencyConflictException(id, depositRequest.getAmount(), account.getBalance());
            }

            return transactionOriginale;
        }
        String sha256Hex = DigestUtils.sha256Hex(String.valueOf(depositRequest));
        String reference = transferNumberGenerator.getNumberGenerator();
        account.credit(depositRequest.getAmount());
        accountSystem.debit(depositRequest.getAmount());

        Transaction transaction = transactionRepository.save(new Transaction(reference,
                TransactionType.DEPOSIT,
                account,
                accountSystem,
                depositRequest.getAmount(),
                Money.zero(depositRequest.getAmount().currency())));


        IdempotencyRecord idempotencyRecord1 =
                new IdempotencyRecord(idempotencyKey, sha256Hex, transaction.getId());

        return transaction;
    }
}
