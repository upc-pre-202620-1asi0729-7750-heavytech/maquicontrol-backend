package com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.PublishMachineryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources.PublishMachineryResource;

public final class PublishMachineryCommandFromResourceAssembler {

    private PublishMachineryCommandFromResourceAssembler() {
    }

    public static PublishMachineryCommand toCommandFromResource(Long ownerProfileId, PublishMachineryResource resource) {
        var rate = resource.hourlyRate();
        var location = resource.location();
        return new PublishMachineryCommand(ownerProfileId, resource.categoryId(), resource.name(), resource.description(),
                resource.brand(), resource.model(), resource.manufactureYear(), rate.amount(), rate.currency(),
                location.department(), location.province(), location.district(), location.address(),
                location.latitude(), location.longitude());
    }
}
