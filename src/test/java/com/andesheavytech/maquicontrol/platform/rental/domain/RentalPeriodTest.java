package com.andesheavytech.maquicontrol.platform.rental.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class RentalPeriodTest {
    @Test void calculatesAnInclusiveDuration() {
        var period = new RentalPeriod(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
        assertEquals(3, period.durationInDays());
    }

    @Test void detectsOverlappingPeriods() {
        var first = new RentalPeriod(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
        var overlapping = new RentalPeriod(LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 14));
        assertTrue(first.overlaps(overlapping));
    }

    @Test void rejectsAnInvertedPeriod() {
        assertThrows(IllegalArgumentException.class, () -> new RentalPeriod(LocalDate.of(2026, 11, 12), LocalDate.of(2026, 11, 10)));
    }
}
