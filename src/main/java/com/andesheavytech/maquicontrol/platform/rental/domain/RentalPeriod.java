package com.andesheavytech.maquicontrol.platform.rental.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/** Inclusive rental date range and overlap policy. */
public record RentalPeriod(LocalDate startDate, LocalDate endDate) {
    public RentalPeriod { Objects.requireNonNull(startDate, "startDate is required"); Objects.requireNonNull(endDate, "endDate is required"); if (endDate.isBefore(startDate)) throw new IllegalArgumentException("endDate must be on or after startDate"); }
    public long durationInDays() { return ChronoUnit.DAYS.between(startDate, endDate) + 1; }
    public boolean overlaps(RentalPeriod other) { return !endDate.isBefore(other.startDate) && !startDate.isAfter(other.endDate); }
}
