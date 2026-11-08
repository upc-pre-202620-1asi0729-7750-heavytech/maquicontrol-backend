package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.events;

public record MaintenanceStatusUpdatedEvent(Long maintenanceId, Long machineryId, String status) {
}
