package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.events;

public record BreakdownReportedEvent(Long maintenanceId, Long machineryId, String severity) {
}
