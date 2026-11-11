package com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Dates of a rental. Both days are included.
 */
public record RentalPeriod(LocalDate startDate, LocalDate endDate) {

    public static final int MAX_DAYS = 365;

    public RentalPeriod {
        Objects.requireNonNull(startDate, "startDate is required");
        Objects.requireNonNull(endDate, "endDate is required");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate cannot be before startDate");
        }
        if (ChronoUnit.DAYS.between(startDate, endDate) + 1 > MAX_DAYS) {
            throw new IllegalArgumentException("a rental cannot last more than %d days".formatted(MAX_DAYS));
        }
    }

    /**
     * Number of days, counting the first and the last day.
     */
    public long durationInDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    public boolean overlaps(RentalPeriod other) {
        return !startDate.isAfter(other.endDate) && !other.startDate.isAfter(endDate);
    }

    public boolean startsBefore(LocalDate date) {
        return startDate.isBefore(date);
    }
}
