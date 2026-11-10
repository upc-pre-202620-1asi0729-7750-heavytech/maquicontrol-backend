package com.andesheavytech.maquicontrol.platform.operations.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.OperationStatus;
import com.andesheavytech.maquicontrol.platform.operations.infrastructure.persistence.jpa.entities.ServiceOperationPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link ServiceOperationPersistenceEntity}.
 */
@Repository
public interface ServiceOperationPersistenceRepository extends JpaRepository<ServiceOperationPersistenceEntity, Long> {

    Optional<ServiceOperationPersistenceEntity> findByRentalId(Long rentalId);

    List<ServiceOperationPersistenceEntity> findByMachineryIdOrderByCreatedAtDesc(Long machineryId);

    List<ServiceOperationPersistenceEntity> findByStatusOrderByCreatedAtDesc(OperationStatus status);

    List<ServiceOperationPersistenceEntity> findByOperatorProfileIdOrderByCreatedAtDesc(Long operatorProfileId);

    boolean existsByRentalId(Long rentalId);
}
