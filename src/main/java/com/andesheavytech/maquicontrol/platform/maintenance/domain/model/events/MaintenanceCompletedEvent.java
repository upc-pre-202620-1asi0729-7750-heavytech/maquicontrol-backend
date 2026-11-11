package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.events;

public record MaintenanceCompletedEvent(Long maintenanceId, Long machineryId) {
}
