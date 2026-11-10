package com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.adapters;

import com.andesheavytech.maquicontrol.platform.shared.infrastructure.events.DomainEventPublisher;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.aggregates.Subscription;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.SubscriptionStatus;
import com.andesheavytech.maquicontrol.platform.subscription.domain.repositories.SubscriptionRepository;
import com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.assemblers.SubscriptionPersistenceAssembler;
import com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.repositories.SubscriptionPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing {@link SubscriptionRepository} with Spring Data JPA.
 */
@Repository
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

    private final SubscriptionPersistenceRepository repository;
    private final DomainEventPublisher domainEventPublisher;

    public SubscriptionRepositoryImpl(SubscriptionPersistenceRepository repository,
                                      DomainEventPublisher domainEventPublisher) {
        this.repository = repository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Subscription save(Subscription subscription) {
        var isNew = subscription.getId() == null;
        var entity = isNew
                ? new SubscriptionPersistenceEntity()
                : repository.findById(subscription.getId())
                        .orElseThrow(() -> new IllegalArgumentException("subscription %d does not exist".formatted(subscription.getId())));

        var saved = repository.saveAndFlush(SubscriptionPersistenceAssembler.copyToPersistence(subscription, entity));
        var savedSubscription = SubscriptionPersistenceAssembler.toDomainFromPersistence(saved);

        if (isNew) {
            savedSubscription.onRequested();
            domainEventPublisher.publishAndClearEvents(savedSubscription);
        } else {
            domainEventPublisher.publishAndClearEvents(subscription);
        }
        return savedSubscription;
    }

    @Override
    public Optional<Subscription> findById(Long id) {
        return repository.findById(id).map(SubscriptionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Subscription> findByOwnerProfileId(Long ownerProfileId) {
        return repository.findFirstByOwnerProfileIdOrderByCreatedAtDesc(ownerProfileId)
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Subscription> findAllByOwnerProfileId(Long ownerProfileId) {
        return repository.findByOwnerProfileIdOrderByCreatedAtDesc(ownerProfileId).stream()
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<Subscription> findActiveByOwnerProfileId(Long ownerProfileId) {
        return repository.findFirstByOwnerProfileIdAndStatusOrderByCreatedAtDesc(
                ownerProfileId, SubscriptionStatus.ACTIVE)
                .map(SubscriptionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsActiveByOwnerProfileId(Long ownerProfileId) {
        return repository.existsByOwnerProfileIdAndStatus(ownerProfileId, SubscriptionStatus.ACTIVE);
    }
}
