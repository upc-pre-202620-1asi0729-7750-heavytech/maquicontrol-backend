package com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Value object representing a work shift on a given date between a start and end time.
 */
public record WorkShift(LocalDate workDate, LocalTime startTime, LocalTime endTime) {

    public WorkShift {
        Objects.requireNonNull(workDate, "workDate is required");
        Objects.requireNonNull(startTime, "startTime is required");
        Objects.requireNonNull(endTime, "endTime is required");
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
    }

    /**
     * Calculates duration in hours with 2 decimal precision.
     */
    public HoursWorked duration() {
        long minutes = Duration.between(startTime, endTime).toMinutes();
        BigDecimal hours = BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        return new HoursWorked(hours);
    }
}
