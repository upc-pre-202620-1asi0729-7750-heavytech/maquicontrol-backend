package com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PlanCode;
import com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.entities.SubscriptionPlanPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link SubscriptionPlanPersistenceEntity}.
 */
@Repository
public interface SubscriptionPlanPersistenceRepository extends JpaRepository<SubscriptionPlanPersistenceEntity, Long> {

    Optional<SubscriptionPlanPersistenceEntity> findByCode(PlanCode code);
}
