package com.ewallet.dto;

import com.ewallet.entity.Money;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Currency;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAccountRequest {

    @NotBlank(message = "ownerName ne peut pas être vide!")
    private String ownerName;

    @NotNull(message = "La devise est obligatoire")
    private Currency currency;
}
