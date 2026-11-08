package com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands;

/**
 * Internal command: a maintenance started on the machine.
 */
public record StartMachineryMaintenanceCommand(Long machineryId) {
}
