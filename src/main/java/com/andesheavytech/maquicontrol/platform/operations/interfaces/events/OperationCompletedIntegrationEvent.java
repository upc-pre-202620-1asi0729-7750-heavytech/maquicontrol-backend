package com.andesheavytech.maquicontrol.platform.operations.interfaces.events;

import java.math.BigDecimal;

/**
 * Integration event emitted when a service operation completes.
 */
public record OperationCompletedIntegrationEvent(Long operationId, Long rentalId, Long machineryId, BigDecimal totalHours) {
}
