package com.andesheavytech.maquicontrol.platform.billing.domain.model.events;

import java.math.BigDecimal;

/**
 * Domain event published when a late penalty is applied to a rental.
 */
public record LatePenaltyAppliedEvent(Long penaltyId, Long rentalId, BigDecimal amount, String currency, String reason) {
}
