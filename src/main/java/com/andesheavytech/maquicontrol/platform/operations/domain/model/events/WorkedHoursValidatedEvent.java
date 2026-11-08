package com.andesheavytech.maquicontrol.platform.operations.domain.model.events;

import java.math.BigDecimal;

/**
 * Domain event published when worked hours are validated by the owner.
 */
public record WorkedHoursValidatedEvent(Long operationId, Long workedHoursId, BigDecimal hours) {
}
