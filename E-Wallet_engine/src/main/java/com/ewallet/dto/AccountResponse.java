package com.ewallet.dto;

import com.ewallet.entity.Money;
import com.ewallet.enumeration.AccountStatus;
import com.ewallet.enumeration.AccountType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    @NonNull
    private UUID id;
    private String accountNumber;
    private String ownerName;
    private Money balance;
    private AccountStatus status;
}