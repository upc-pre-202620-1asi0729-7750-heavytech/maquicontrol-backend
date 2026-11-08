package com.andesheavytech.maquicontrol.platform.operations.domain.model.events;

/**
 * Domain event published when worked hours are rejected by the owner.
 */
public record WorkedHoursRejectedEvent(Long operationId, Long workedHoursId) {
}
