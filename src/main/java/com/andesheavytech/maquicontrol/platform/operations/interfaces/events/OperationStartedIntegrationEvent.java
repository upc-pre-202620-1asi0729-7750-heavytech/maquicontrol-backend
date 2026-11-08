package com.andesheavytech.maquicontrol.platform.operations.interfaces.events;

/**
 * Integration event emitted when a service operation starts.
 */
public record OperationStartedIntegrationEvent(Long operationId, Long rentalId, Long machineryId) {
}
