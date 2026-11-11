package com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RentalPeriodTest {

    @Test
    void calculatesAnInclusiveDuration() {
        var period = new RentalPeriod(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
        assertEquals(3, period.durationInDays());
    }

    @Test
    void detectsOverlappingPeriods() {
        var first = new RentalPeriod(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
        var overlapping = new RentalPeriod(LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 14));
        assertTrue(first.overlaps(overlapping));
    }

    @Test
    void consecutivePeriodsDoNotOverlap() {
        var first = new RentalPeriod(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
        var next = new RentalPeriod(LocalDate.of(2026, 11, 13), LocalDate.of(2026, 11, 14));
        assertFalse(first.overlaps(next));
    }

    @Test
    void rejectsAnInvertedPeriod() {
        assertThrows(IllegalArgumentException.class,
                () -> new RentalPeriod(LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 10)));
    }
}
