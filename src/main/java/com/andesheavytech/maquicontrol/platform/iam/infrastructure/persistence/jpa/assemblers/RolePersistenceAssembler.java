package com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.entities.Role;
import com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;

/**
 * Maps between {@link Role} and {@link RolePersistenceEntity}.
 */
public final class RolePersistenceAssembler {

    private RolePersistenceAssembler() {
    }

    public static Role toDomainFromPersistence(RolePersistenceEntity entity) {
        return new Role(entity.getId(), entity.getName(), entity.getDescription());
    }

    public static RolePersistenceEntity toPersistenceFromDomain(Role role) {
        var entity = new RolePersistenceEntity();
        if (role.getId() != null) {
            entity.setId(role.getId());
        }
        entity.setName(role.getName());
        entity.setDescription(role.getDescription());
        return entity;
    }
}
