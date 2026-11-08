package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.events;

public record MaintenanceScheduledEvent(Long maintenanceId, Long machineryId, String type) {
}
