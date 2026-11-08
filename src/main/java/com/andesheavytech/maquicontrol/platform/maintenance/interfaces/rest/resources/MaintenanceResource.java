package com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.util.List;

public record MaintenanceResource(Long id, Long machineryId, String type, String status, String description,
                                  LocalDateTime scheduledDate, LocalDateTime startedAt, LocalDateTime completedAt,
                                  String technicianName, MaintenanceCostResource cost,
                                  List<BreakdownReportResource> breakdownReports) {
}
