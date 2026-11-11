package com.andesheavytech.maquicontrol.platform.operations.domain.repositories;

import com.andesheavytech.maquicontrol.platform.operations.domain.model.aggregates.ServiceOperation;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.OperationStatus;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for {@link ServiceOperation}.
 */
public interface ServiceOperationRepository {

    ServiceOperation save(ServiceOperation operation);

    Optional<ServiceOperation> findById(Long id);

    Optional<ServiceOperation> findByRentalId(Long rentalId);

    List<ServiceOperation> findByMachineryId(Long machineryId);

    List<ServiceOperation> findByStatus(OperationStatus status);

    List<ServiceOperation> findByOperatorProfileId(Long operatorProfileId);

    boolean existsByRentalId(Long rentalId);
}
