package com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Machinery;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources.MachineryLocationResource;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources.MachineryResource;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources.MoneyResource;

public final class MachineryResourceFromEntityAssembler {

    private MachineryResourceFromEntityAssembler() {
    }

    public static MachineryResource toResourceFromEntity(Machinery machinery) {
        var location = machinery.getLocation();
        var specification = machinery.getSpecification();
        return new MachineryResource(machinery.getId(), machinery.getOwnerProfileId(), machinery.getCategoryId(),
                machinery.getName(), machinery.getDescription(), specification.brand(), specification.model(),
                specification.manufactureYear(),
                new MoneyResource(machinery.getHourlyRate().amount(), machinery.getHourlyRate().currency().name()),
                new MachineryLocationResource(location.department(), location.province(), location.district(),
                        location.address(), location.latitude(), location.longitude()),
                machinery.getStatus().name());
    }
}
