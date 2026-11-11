package com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value object representing worked hours as a non-negative decimal quantity.
 */
public record HoursWorked(BigDecimal value) {

    public static final HoursWorked ZERO = new HoursWorked(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));

    public HoursWorked {
        Objects.requireNonNull(value, "value is required");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("worked hours cannot be negative");
        }
        value = value.setScale(2, RoundingMode.HALF_UP);
    }

    public HoursWorked add(HoursWorked other) {
        if (other == null) {
            return this;
        }
        return new HoursWorked(this.value.add(other.value));
    }

    public boolean isNonNegative() {
        return value.compareTo(BigDecimal.ZERO) >= 0;
    }
}
