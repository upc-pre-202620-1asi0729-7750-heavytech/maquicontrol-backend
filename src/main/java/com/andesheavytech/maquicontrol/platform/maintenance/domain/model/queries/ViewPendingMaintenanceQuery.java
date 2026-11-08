package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries;

/**
 * Scheduled and in-progress maintenances of the owner's machines.
 */
public record ViewPendingMaintenanceQuery(Long ownerProfileId) {
}
