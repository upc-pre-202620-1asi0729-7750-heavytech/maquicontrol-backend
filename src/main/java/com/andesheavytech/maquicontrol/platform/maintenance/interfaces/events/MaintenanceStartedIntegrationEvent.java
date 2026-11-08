package com.andesheavytech.maquicontrol.platform.maintenance.interfaces.events;

/**
 * Published for other bounded contexts when a maintenance starts.
 */
public record MaintenanceStartedIntegrationEvent(Long maintenanceId, Long machineryId) {
}
