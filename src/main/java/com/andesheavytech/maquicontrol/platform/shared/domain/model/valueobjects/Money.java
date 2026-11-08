package com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable monetary amount shared by the bounded contexts that handle prices.
 *
 * @param amount   the non-negative amount, rounded to two decimals
 * @param currency the currency the amount is expressed in
 */
public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Objects.requireNonNull(amount, "amount is required");
        Objects.requireNonNull(currency, "currency is required");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public Money(BigDecimal amount, String currencyCode) {
        this(amount, Currency.fromCode(currencyCode));
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money multiply(BigDecimal factor) {
        Objects.requireNonNull(factor, "factor is required");
        if (factor.signum() < 0) {
            throw new IllegalArgumentException("factor cannot be negative");
        }
        return new Money(amount.multiply(factor), currency);
    }

    public Money multiply(long factor) {
        return multiply(BigDecimal.valueOf(factor));
    }

    public boolean isNonNegative() {
        return amount.signum() >= 0;
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "other is required");
        if (other.currency != currency) {
            throw new IllegalArgumentException("cannot combine %s with %s".formatted(currency, other.currency));
        }
    }
}
