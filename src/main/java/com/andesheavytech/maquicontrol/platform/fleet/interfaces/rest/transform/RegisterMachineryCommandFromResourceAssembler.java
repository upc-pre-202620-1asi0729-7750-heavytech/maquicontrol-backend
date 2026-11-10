package com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.RegisterMachineryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources.RegisterMachineryResource;

public final class RegisterMachineryCommandFromResourceAssembler {

    private RegisterMachineryCommandFromResourceAssembler() {
    }

    public static RegisterMachineryCommand toCommandFromResource(Long ownerProfileId, RegisterMachineryResource resource) {
        var rate = resource.hourlyRate();
        var location = resource.location();
        return new RegisterMachineryCommand(ownerProfileId, resource.categoryId(), resource.name(), resource.description(),
                resource.brand(), resource.model(), resource.manufactureYear(), rate.amount(), rate.currency(),
                location.department(), location.province(), location.district(), location.address(),
                location.latitude(), location.longitude());
    }
}
