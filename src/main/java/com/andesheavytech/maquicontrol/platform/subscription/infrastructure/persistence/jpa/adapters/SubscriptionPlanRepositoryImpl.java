package com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.adapters;

import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.SubscriptionPlan;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PlanCode;
import com.andesheavytech.maquicontrol.platform.subscription.domain.repositories.SubscriptionPlanRepository;
import com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.assemblers.SubscriptionPlanPersistenceAssembler;
import com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.entities.SubscriptionPlanPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.repositories.SubscriptionPlanPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing {@link SubscriptionPlanRepository} with Spring Data JPA.
 */
@Repository
public class SubscriptionPlanRepositoryImpl implements SubscriptionPlanRepository {

    private final SubscriptionPlanPersistenceRepository repository;

    public SubscriptionPlanRepositoryImpl(SubscriptionPlanPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public SubscriptionPlan save(SubscriptionPlan plan) {
        var entity = plan.getId() == null
                ? new SubscriptionPlanPersistenceEntity()
                : repository.findById(plan.getId()).orElse(new SubscriptionPlanPersistenceEntity());

        var saved = repository.saveAndFlush(SubscriptionPlanPersistenceAssembler.copyToPersistence(plan, entity));
        return SubscriptionPlanPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public Optional<SubscriptionPlan> findById(Long id) {
        return repository.findById(id).map(SubscriptionPlanPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<SubscriptionPlan> findByCode(PlanCode code) {
        return repository.findByCode(code).map(SubscriptionPlanPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<SubscriptionPlan> findAll() {
        return repository.findAll().stream()
                .map(SubscriptionPlanPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public long count() {
        return repository.count();
    }
}
