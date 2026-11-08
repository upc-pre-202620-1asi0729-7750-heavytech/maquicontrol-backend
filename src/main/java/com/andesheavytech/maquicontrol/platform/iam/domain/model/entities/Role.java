package com.andesheavytech.maquicontrol.platform.iam.domain.model.entities;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.RoleName;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/**
 * Role entity. Identified by its {@link RoleName}.
 */
@Getter
@EqualsAndHashCode(of = "name")
public class Role {

    private final Long id;
    private final RoleName name;
    private final String description;

    public Role(Long id, RoleName name, String description) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name is required");
        this.description = description == null ? "" : description;
    }

    public Role(RoleName name) {
        this(null, name, name.defaultDescription());
    }

    public boolean isAdministrative() {
        return name == RoleName.SYSTEM_ADMINISTRATOR || name == RoleName.FLEET_ADMINISTRATOR;
    }
}
