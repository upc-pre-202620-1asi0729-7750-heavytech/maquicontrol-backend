package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries;

/**
 * Every maintenance of the owner's machines, optionally only for one machine.
 *
 * @param machineryId a machine of the owner, or {@code null} for all of them
 */
public record GetMaintenancesByOwnerProfileIdQuery(Long ownerProfileId, Long machineryId) {
}
