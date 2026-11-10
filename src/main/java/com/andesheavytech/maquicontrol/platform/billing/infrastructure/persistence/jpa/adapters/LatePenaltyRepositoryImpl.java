package com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.adapters;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.LatePenalty;
import com.andesheavytech.maquicontrol.platform.billing.domain.repositories.LatePenaltyRepository;
import com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.assemblers.LatePenaltyPersistenceAssembler;
import com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.entities.LatePenaltyPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.repositories.LatePenaltyPersistenceRepository;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements {@link LatePenaltyRepository} with Spring Data JPA.
 */
@Repository
public class LatePenaltyRepositoryImpl implements LatePenaltyRepository {

    private final LatePenaltyPersistenceRepository latePenaltyPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public LatePenaltyRepositoryImpl(LatePenaltyPersistenceRepository latePenaltyPersistenceRepository,
                                     DomainEventPublisher domainEventPublisher) {
        this.latePenaltyPersistenceRepository = latePenaltyPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public LatePenalty save(LatePenalty penalty) {
        var isNew = penalty.getId() == null;
        var entity = isNew
                ? new LatePenaltyPersistenceEntity()
                : latePenaltyPersistenceRepository.findById(penalty.getId())
                        .orElseThrow(() -> new IllegalArgumentException("penalty %d does not exist".formatted(penalty.getId())));

        var saved = latePenaltyPersistenceRepository.saveAndFlush(
                LatePenaltyPersistenceAssembler.copyToPersistence(penalty, entity));
        var savedPenalty = LatePenaltyPersistenceAssembler.toDomainFromPersistence(saved);

        if (isNew) {
            savedPenalty.onApplied();
            domainEventPublisher.publishAndClearEvents(savedPenalty);
        } else {
            domainEventPublisher.publishAndClearEvents(penalty);
        }
        return savedPenalty;
    }

    @Override
    public Optional<LatePenalty> findById(Long id) {
        return latePenaltyPersistenceRepository.findById(id)
                .map(LatePenaltyPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<LatePenalty> findByRentalId(Long rentalId) {
        return latePenaltyPersistenceRepository.findByRentalId(rentalId).stream()
                .map(LatePenaltyPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<LatePenalty> findByRentalIdIn(Collection<Long> rentalIds) {
        if (rentalIds == null || rentalIds.isEmpty()) {
            return List.of();
        }
        return latePenaltyPersistenceRepository.findByRentalIdIn(rentalIds).stream()
                .map(LatePenaltyPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<LatePenalty> findAll() {
        return latePenaltyPersistenceRepository.findAll().stream()
                .map(LatePenaltyPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}
