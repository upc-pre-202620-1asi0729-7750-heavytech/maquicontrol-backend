package com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalPeriod;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalStatus;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RentalTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 9);

    private Rental requestedRental() {
        var period = new RentalPeriod(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
        var rental = Rental.request(1L, 2L, period, new Money(new BigDecimal("150.00"), "PEN"), TODAY);
        return Rental.reconstitute(10L, rental.getMachineryId(), rental.getContractorProfileId(), rental.getPeriod(),
                rental.getTotalAmount(), rental.getStatus(), null, rental.getRequestedAt(), null, null);
    }

    @Test
    void totalIsHourlyRateTimesEightHoursPerDay() {
        assertEquals(new BigDecimal("3600.00"), requestedRental().getTotalAmount().amount());
    }

    @Test
    void rejectsAStartDateInThePast() {
        var period = new RentalPeriod(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 2));
        assertThrows(IllegalArgumentException.class,
                () -> Rental.request(1L, 2L, period, new Money(BigDecimal.TEN, "PEN"), TODAY));
    }

    @Test
    void followsTheLifecycleFromRequestToCompletion() {
        var rental = requestedRental();
        rental.confirm();
        assertEquals(RentalStatus.CONFIRMED, rental.getStatus());
        rental.start();
        assertEquals(RentalStatus.IN_PROGRESS, rental.getStatus());
        rental.complete();
        assertEquals(RentalStatus.COMPLETED, rental.getStatus());
    }

    @Test
    void canOnlyBeConfirmedWhileRequested() {
        var rental = requestedRental();
        rental.reject("Machine not available");
        assertThrows(IllegalStateException.class, rental::confirm);
    }

    @Test
    void cancellingRequiresAReason() {
        assertThrows(IllegalArgumentException.class, () -> requestedRental().cancel(" "));
    }

    @Test
    void aCompletedRentalCannotBeCancelled() {
        var rental = requestedRental();
        rental.confirm();
        rental.start();
        rental.complete();
        assertThrows(IllegalStateException.class, () -> rental.cancel("Too late"));
    }
}
