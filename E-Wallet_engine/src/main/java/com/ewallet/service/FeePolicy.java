package com.ewallet.service;

import com.ewallet.entity.Money;

public interface FeePolicy {
    public Money calculateFee(Money amount);
}
