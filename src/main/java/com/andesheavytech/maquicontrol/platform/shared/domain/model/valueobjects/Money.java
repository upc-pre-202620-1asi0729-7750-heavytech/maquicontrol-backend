package com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/** Immutable monetary amount used by bounded contexts without persistence concerns. */
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "amount is required");
        Objects.requireNonNull(currency, "currency is required");
        if (amount.signum() < 0) throw new IllegalArgumentException("amount cannot be negative");
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public Money multiply(long factor) {
        if (factor < 0) throw new IllegalArgumentException("factor cannot be negative");
        return new Money(amount.multiply(BigDecimal.valueOf(factor)), currency);
    }
}
