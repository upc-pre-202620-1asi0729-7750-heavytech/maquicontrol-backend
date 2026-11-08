package com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.entities.InvoicePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link InvoicePersistenceEntity}.
 */
@Repository
public interface InvoicePersistenceRepository extends JpaRepository<InvoicePersistenceEntity, Long> {

    Optional<InvoicePersistenceEntity> findByRentalId(Long rentalId);

    List<InvoicePersistenceEntity> findByRentalIdIn(Collection<Long> rentalIds);

    boolean existsByRentalId(Long rentalId);
}
