package com.andesheavytech.maquicontrol.platform.subscription.domain.repositories;

import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.SubscriptionPlan;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PlanCode;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for {@link SubscriptionPlan}.
 */
public interface SubscriptionPlanRepository {

    SubscriptionPlan save(SubscriptionPlan plan);

    Optional<SubscriptionPlan> findById(Long id);

    Optional<SubscriptionPlan> findByCode(PlanCode code);

    List<SubscriptionPlan> findAll();

    long count();
}
