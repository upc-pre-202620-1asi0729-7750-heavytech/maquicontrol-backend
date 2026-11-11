package com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.jpa.adapters;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.entities.Role;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.RoleName;
import com.andesheavytech.maquicontrol.platform.iam.domain.repositories.RoleRepository;
import com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.jpa.assemblers.RolePersistenceAssembler;
import com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.jpa.repositories.RolePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements {@link RoleRepository} with Spring Data JPA.
 */
@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final RolePersistenceRepository rolePersistenceRepository;

    public RoleRepositoryImpl(RolePersistenceRepository rolePersistenceRepository) {
        this.rolePersistenceRepository = rolePersistenceRepository;
    }

    @Override
    public Role save(Role role) {
        var saved = rolePersistenceRepository.save(RolePersistenceAssembler.toPersistenceFromDomain(role));
        return RolePersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public Optional<Role> findByName(RoleName name) {
        return rolePersistenceRepository.findByName(name).map(RolePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Role> findAll() {
        return rolePersistenceRepository.findAll().stream()
                .map(RolePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsByName(RoleName name) {
        return rolePersistenceRepository.existsByName(name);
    }
}
