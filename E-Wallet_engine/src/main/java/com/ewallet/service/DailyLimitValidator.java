package com.ewallet.service;

import com.ewallet.entity.Account;
import com.ewallet.entity.Money;
import com.ewallet.exception.DailyLimitExceededException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyLimitValidator {

    @Transactional
    public void check(Account account, Money amount) {
        if (amount.isGreaterThan(account.getDailyLimit())) {
            throw new DailyLimitExceededException(account.getId(), account.getBalance(), amount);
        }
    }
}
