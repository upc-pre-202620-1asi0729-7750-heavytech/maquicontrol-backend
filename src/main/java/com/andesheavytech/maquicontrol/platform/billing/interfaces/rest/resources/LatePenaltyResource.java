package com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representation of a late penalty surcharge resource.
 */
public record LatePenaltyResource(
        Long id,
        Long rentalId,
        BigDecimal amount,
        String currency,
        String reason,
        LocalDateTime appliedAt
) {
}
