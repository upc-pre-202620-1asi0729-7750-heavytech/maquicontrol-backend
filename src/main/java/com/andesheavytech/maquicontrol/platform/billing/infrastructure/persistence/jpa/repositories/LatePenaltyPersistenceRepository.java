package com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.entities.LatePenaltyPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Spring Data JPA repository for {@link LatePenaltyPersistenceEntity}.
 */
@Repository
public interface LatePenaltyPersistenceRepository extends JpaRepository<LatePenaltyPersistenceEntity, Long> {

    List<LatePenaltyPersistenceEntity> findByRentalId(Long rentalId);

    List<LatePenaltyPersistenceEntity> findByRentalIdIn(Collection<Long> rentalIds);
}
