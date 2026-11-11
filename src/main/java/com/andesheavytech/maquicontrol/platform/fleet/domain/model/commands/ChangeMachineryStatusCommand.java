package com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects.MachineryStatus;

public record ChangeMachineryStatusCommand(Long machineryId, Long ownerProfileId, MachineryStatus status) {
}
