package com.andesheavytech.maquicontrol.platform.maintenance.interfaces.events;

/**
 * Published for other bounded contexts when a maintenance is completed.
 */
public record MaintenanceCompletedIntegrationEvent(Long maintenanceId, Long machineryId) {
}
