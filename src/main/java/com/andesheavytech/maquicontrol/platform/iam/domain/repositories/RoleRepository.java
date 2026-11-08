package com.andesheavytech.maquicontrol.platform.iam.domain.repositories;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.entities.Role;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.RoleName;

import java.util.List;
import java.util.Optional;

/**
 * Domain port for the role catalog.
 */
public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findByName(RoleName name);

    List<Role> findAll();

    boolean existsByName(RoleName name);
}
