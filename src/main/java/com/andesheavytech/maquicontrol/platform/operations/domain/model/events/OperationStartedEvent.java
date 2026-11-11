package com.andesheavytech.maquicontrol.platform.operations.domain.model.events;

/**
 * Domain event published when a service operation starts.
 */
public record OperationStartedEvent(Long operationId, Long rentalId, Long machineryId) {
}
