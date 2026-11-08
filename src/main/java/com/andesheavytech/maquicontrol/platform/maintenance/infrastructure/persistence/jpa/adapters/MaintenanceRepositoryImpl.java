package com.andesheavytech.maquicontrol.platform.maintenance.infrastructure.persistence.jpa.adapters;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.aggregates.Maintenance;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceStatus;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceType;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.repositories.MaintenanceRepository;
import com.andesheavytech.maquicontrol.platform.maintenance.infrastructure.persistence.jpa.assemblers.MaintenancePersistenceAssembler;
import com.andesheavytech.maquicontrol.platform.maintenance.infrastructure.persistence.jpa.entities.MaintenancePersistenceEntity;
import com.andesheavytech.maquicontrol.platform.maintenance.infrastructure.persistence.jpa.repositories.MaintenancePersistenceRepository;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements {@link MaintenanceRepository} with Spring Data JPA. Existing rows are
 * loaded and updated in place, so the optimistic-locking {@code version} column is respected.
 */
@Repository
public class MaintenanceRepositoryImpl implements MaintenanceRepository {

    private static final EnumSet<MaintenanceStatus> PENDING =
            EnumSet.of(MaintenanceStatus.SCHEDULED, MaintenanceStatus.IN_PROGRESS);

    private final MaintenancePersistenceRepository maintenancePersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public MaintenanceRepositoryImpl(MaintenancePersistenceRepository maintenancePersistenceRepository,
                                     DomainEventPublisher domainEventPublisher) {
        this.maintenancePersistenceRepository = maintenancePersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Maintenance save(Maintenance maintenance) {
        var isNew = maintenance.getId() == null;
        var entity = isNew
                ? new MaintenancePersistenceEntity()
                : maintenancePersistenceRepository.findById(maintenance.getId())
                        .orElseThrow(() -> new IllegalArgumentException("maintenance %d does not exist".formatted(maintenance.getId())));
        var saved = maintenancePersistenceRepository.saveAndFlush(
                MaintenancePersistenceAssembler.copyToPersistence(maintenance, entity));
        var savedMaintenance = MaintenancePersistenceAssembler.toDomainFromPersistence(saved);
        if (isNew) {
            savedMaintenance.onScheduled();
            domainEventPublisher.publishAndClearEvents(savedMaintenance);
        } else {
            domainEventPublisher.publishAndClearEvents(maintenance);
        }
        return savedMaintenance;
    }

    @Override
    public Optional<Maintenance> findById(Long id) {
        return maintenancePersistenceRepository.findById(id).map(MaintenancePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Maintenance> findByMachineryId(Long machineryId) {
        return toDomain(maintenancePersistenceRepository.findByMachineryIdOrderByScheduledDateDesc(machineryId));
    }

    @Override
    public List<Maintenance> findByMachineryIds(Collection<Long> machineryIds) {
        return toDomain(maintenancePersistenceRepository.findByMachineryIdInOrderByScheduledDateDesc(machineryIds));
    }

    @Override
    public List<Maintenance> findByStatus(MaintenanceStatus status) {
        return toDomain(maintenancePersistenceRepository.findByStatusOrderByScheduledDateAsc(status));
    }

    @Override
    public List<Maintenance> findCompletedByMachineryId(Long machineryId) {
        return toDomain(maintenancePersistenceRepository.findByMachineryIdAndStatusOrderByCompletedAtDesc(
                machineryId, MaintenanceStatus.COMPLETED));
    }

    @Override
    public List<Maintenance> findPendingByMachineryIds(Collection<Long> machineryIds) {
        return toDomain(maintenancePersistenceRepository.findByMachineryIdInAndStatusInOrderByScheduledDateAsc(
                machineryIds, PENDING));
    }

    @Override
    public Optional<Maintenance> findPendingCorrectiveByMachineryId(Long machineryId) {
        return maintenancePersistenceRepository.findFirstByMachineryIdAndTypeAndStatusInOrderByIdDesc(
                        machineryId, MaintenanceType.CORRECTIVE, PENDING)
                .map(MaintenancePersistenceAssembler::toDomainFromPersistence);
    }

    private static List<Maintenance> toDomain(List<MaintenancePersistenceEntity> entities) {
        return entities.stream().map(MaintenancePersistenceAssembler::toDomainFromPersistence).toList();
    }
}
