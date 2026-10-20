package com.ewallet.exception;

import lombok.Getter;

import java.util.Currency;
/**
 * Levée par le Value Object Money lorsqu'une opération arithmétique
 * tente de mélanger deux devises (ex: EUR.add(USD)).
 * C'est une exception de VIOLATION D'INVARIANT : si elle se déclenche,
 * c'est qu'un bug applicatif a laissé passer des montants hétérogènes
 * jusqu'au domaine. Le handler la traduit néanmoins en 422 propre
 * (défense en profondeur) plutôt qu'en 500 opaque.
 */
@Getter
public class CurrencyMismatchException extends BusinessException {

    public static final String ERROR_CODE = "CURRENCY_MISMATCH";

    private final Currency source;  // devise attendue (celle de l'opération en cours)
    private final Currency target;  // devise reçue (celle de l'opérande fautif)

    public CurrencyMismatchException(Currency source, Currency target) {
        super(ERROR_CODE,
                "Opération interdite entre devises différentes : %s vs %s"
                        .formatted(source.getCurrencyCode(), target.getCurrencyCode()));
        this.source = source;
        this.target = target;
    }
}