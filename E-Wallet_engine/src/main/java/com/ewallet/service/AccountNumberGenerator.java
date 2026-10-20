package com.ewallet.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AccountNumberGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public String generate() {
        StringBuilder accountNumber = new StringBuilder("EW-");

        for (int i = 0; i < 12; i++) {

            int digit = RANDOM.nextInt(10);

            accountNumber.append(digit);
        }

        return accountNumber.toString();
    }
}
