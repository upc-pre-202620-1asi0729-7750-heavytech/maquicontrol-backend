package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.events;

public record MaintenanceStartedEvent(Long maintenanceId, Long machineryId) {
}
