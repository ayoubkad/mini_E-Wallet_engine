package com.ewallet.service;

import com.ewallet.entity.Money;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PercentageFeePolicy implements FeePolicy{

    @Override
    public Money calculateFee(Money amount) {
        return amount
                .multiply(BigDecimal.valueOf(0.02), RoundingMode.HALF_EVEN);
    }

}
