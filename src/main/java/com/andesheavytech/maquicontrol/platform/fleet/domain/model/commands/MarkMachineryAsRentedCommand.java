package com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands;

/**
 * Internal command: a rental started and the machine is at work.
 */
public record MarkMachineryAsRentedCommand(Long machineryId) {
}
