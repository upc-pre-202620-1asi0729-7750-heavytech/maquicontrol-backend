package com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.commands.CreateOrganizationCommand;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.OrganizationType;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources.CreateOrganizationResource;

public final class CreateOrganizationCommandFromResourceAssembler {

    private CreateOrganizationCommandFromResourceAssembler() {
    }

    public static CreateOrganizationCommand toCommandFromResource(Long userAccountId, CreateOrganizationResource resource) {
        return new CreateOrganizationCommand(userAccountId, resource.legalName(), resource.tradeName(), resource.taxId(),
                EnumFromStringAssembler.toEnum(OrganizationType.class, resource.type(), "type"), resource.address());
    }
}
