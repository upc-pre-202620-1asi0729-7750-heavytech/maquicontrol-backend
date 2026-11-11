package com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/**
 * Body of {@code PATCH /api/v1/maintenances/{maintenanceId}/status}: IN_PROGRESS, CANCELLED, or
 * COMPLETED with an optional cost.
 */
public record UpdateMaintenanceStatusResource(@NotBlank String status, MaintenanceCostResource cost) {
}
