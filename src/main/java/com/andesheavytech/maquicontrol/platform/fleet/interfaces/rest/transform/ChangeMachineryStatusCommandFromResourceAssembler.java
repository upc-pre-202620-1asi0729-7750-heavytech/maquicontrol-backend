package com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.ChangeMachineryStatusCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects.MachineryStatus;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources.ChangeMachineryStatusResource;

import java.util.Arrays;
import java.util.Locale;

public final class ChangeMachineryStatusCommandFromResourceAssembler {

    private ChangeMachineryStatusCommandFromResourceAssembler() {
    }

    public static ChangeMachineryStatusCommand toCommandFromResource(Long machineryId, Long ownerProfileId,
                                                                     ChangeMachineryStatusResource resource) {
        var normalized = resource.status().trim().toUpperCase(Locale.ROOT);
        var status = Arrays.stream(MachineryStatus.values())
                .filter(value -> value.name().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("status %s is not valid".formatted(resource.status())));
        return new ChangeMachineryStatusCommand(machineryId, ownerProfileId, status);
    }
}
