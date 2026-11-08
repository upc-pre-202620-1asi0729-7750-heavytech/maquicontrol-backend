package com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.Reason;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LatePenaltyTest {

    @Test
    void applyCreatesLatePenaltyWithAppliedAtTimestamp() {
        var penalty = LatePenalty.apply(1L, new Money(new BigDecimal("300.00"), "PEN"),
                new Reason("Returned 3 days late"));

        assertEquals(1L, penalty.getRentalId());
        assertEquals(new BigDecimal("300.00"), penalty.getAmount().amount());
        assertEquals("Returned 3 days late", penalty.getReason().value());
        assertNotNull(penalty.getAppliedAt());
    }

    @Test
    void applyValidatesRentalIdAndReason() {
        assertThrows(IllegalArgumentException.class, () -> LatePenalty.apply(null,
                new Money(BigDecimal.TEN, "PEN"), new Reason("Late")));
        assertThrows(NullPointerException.class, () -> LatePenalty.apply(1L,
                null, new Reason("Late")));
        assertThrows(IllegalArgumentException.class, () -> LatePenalty.apply(1L,
                new Money(BigDecimal.TEN, "PEN"), new Reason("")));
    }
}
