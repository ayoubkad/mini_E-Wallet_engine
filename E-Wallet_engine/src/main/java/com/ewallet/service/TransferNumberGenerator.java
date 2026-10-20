package com.ewallet.service;


import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class TransferNumberGenerator {

    public static final SecureRandom NUMBER_GENERATOR = new SecureRandom();

    public String getNumberGenerator() {
        StringBuilder a = new StringBuilder("TXN-");

        for (int i = 0; i < 11; i++) {

            int digits = NUMBER_GENERATOR.nextInt();

            a.append(digits);
        }

        return a.toString();
    }
}
