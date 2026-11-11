package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.events;

public record MaintenanceCancelledEvent(Long maintenanceId, Long machineryId) {
}
