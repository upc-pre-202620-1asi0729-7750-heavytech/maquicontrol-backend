package com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.entities.Organization;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources.OrganizationResource;

public final class OrganizationResourceFromEntityAssembler {

    private OrganizationResourceFromEntityAssembler() {
    }

    public static OrganizationResource toResourceFromEntity(Organization organization) {
        if (organization == null) {
            return null;
        }
        return new OrganizationResource(organization.getId(), organization.getLegalName(), organization.getTradeName(),
                organization.getTaxId().value(), organization.getType().name(), organization.getAddress());
    }
}
