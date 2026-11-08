package com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.entities.Role;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.resources.RoleResource;

public final class RoleResourceFromEntityAssembler {

    private RoleResourceFromEntityAssembler() {
    }

    public static RoleResource toResourceFromEntity(Role role) {
        return new RoleResource(role.getId(), role.getName().name(), role.getDescription());
    }
}
