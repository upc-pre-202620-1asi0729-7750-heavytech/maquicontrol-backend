package com.andesheavytech.maquicontrol.platform.operations.infrastructure.persistence.jpa.adapters;

import com.andesheavytech.maquicontrol.platform.operations.domain.model.aggregates.ServiceOperation;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.OperationStatus;
import com.andesheavytech.maquicontrol.platform.operations.domain.repositories.ServiceOperationRepository;
import com.andesheavytech.maquicontrol.platform.operations.infrastructure.persistence.jpa.assemblers.ServiceOperationPersistenceAssembler;
import com.andesheavytech.maquicontrol.platform.operations.infrastructure.persistence.jpa.entities.ServiceOperationPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.operations.infrastructure.persistence.jpa.repositories.ServiceOperationPersistenceRepository;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements {@link ServiceOperationRepository} with Spring Data JPA.
 */
@Repository
public class ServiceOperationRepositoryImpl implements ServiceOperationRepository {

    private final ServiceOperationPersistenceRepository serviceOperationPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public ServiceOperationRepositoryImpl(ServiceOperationPersistenceRepository serviceOperationPersistenceRepository,
                                          DomainEventPublisher domainEventPublisher) {
        this.serviceOperationPersistenceRepository = serviceOperationPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public ServiceOperation save(ServiceOperation operation) {
        var isNew = operation.getId() == null;
        var entity = isNew
                ? new ServiceOperationPersistenceEntity()
                : serviceOperationPersistenceRepository.findById(operation.getId())
                        .orElseThrow(() -> new IllegalArgumentException("service operation %d does not exist".formatted(operation.getId())));

        var saved = serviceOperationPersistenceRepository.saveAndFlush(
                ServiceOperationPersistenceAssembler.copyToPersistence(operation, entity));
        var savedOperation = ServiceOperationPersistenceAssembler.toDomainFromPersistence(saved);

        if (isNew) {
            savedOperation.onStarted();
            domainEventPublisher.publishAndClearEvents(savedOperation);
        } else {
            domainEventPublisher.publishAndClearEvents(operation);
        }
        return savedOperation;
    }

    @Override
    public Optional<ServiceOperation> findById(Long id) {
        return serviceOperationPersistenceRepository.findById(id)
                .map(ServiceOperationPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<ServiceOperation> findByRentalId(Long rentalId) {
        return serviceOperationPersistenceRepository.findByRentalId(rentalId)
                .map(ServiceOperationPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<ServiceOperation> findByMachineryId(Long machineryId) {
        return serviceOperationPersistenceRepository.findByMachineryIdOrderByCreatedAtDesc(machineryId).stream()
                .map(ServiceOperationPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<ServiceOperation> findByStatus(OperationStatus status) {
        return serviceOperationPersistenceRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(ServiceOperationPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<ServiceOperation> findByOperatorProfileId(Long operatorProfileId) {
        return serviceOperationPersistenceRepository.findByOperatorProfileIdOrderByCreatedAtDesc(operatorProfileId).stream()
                .map(ServiceOperationPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsByRentalId(Long rentalId) {
        return serviceOperationPersistenceRepository.existsByRentalId(rentalId);
    }
}
