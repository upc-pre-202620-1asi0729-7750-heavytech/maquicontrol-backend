package com.andesheavytech.maquicontrol.platform.subscription.application.internal.queryservices;

import com.andesheavytech.maquicontrol.platform.subscription.application.queryservices.SubscriptionManagementQueryService;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.aggregates.Subscription;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.Payment;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.SubscriptionPlan;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.GetPlanByCodeQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.GetPlanByIdQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.GetSubscriptionByIdQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.ViewPaymentsQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.ViewPlansQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.ViewSubscriptionQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.repositories.SubscriptionPlanRepository;
import com.andesheavytech.maquicontrol.platform.subscription.domain.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link SubscriptionManagementQueryService}.
 */
@Service
public class SubscriptionManagementQueryServiceImpl implements SubscriptionManagementQueryService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionManagementQueryServiceImpl(SubscriptionRepository subscriptionRepository,
                                                  SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    @Override
    public List<SubscriptionPlan> handle(ViewPlansQuery query) {
        return subscriptionPlanRepository.findAll();
    }

    @Override
    public Optional<SubscriptionPlan> handle(GetPlanByIdQuery query) {
        return subscriptionPlanRepository.findById(query.planId());
    }

    @Override
    public Optional<SubscriptionPlan> handle(GetPlanByCodeQuery query) {
        return subscriptionPlanRepository.findByCode(query.code());
    }

    @Override
    public Optional<Subscription> handle(ViewSubscriptionQuery query) {
        return subscriptionRepository.findActiveByOwnerProfileId(query.ownerProfileId())
                .or(() -> subscriptionRepository.findByOwnerProfileId(query.ownerProfileId()));
    }

    @Override
    public Optional<Subscription> handle(GetSubscriptionByIdQuery query) {
        return subscriptionRepository.findById(query.subscriptionId());
    }

    @Override
    public List<Payment> handle(ViewPaymentsQuery query) {
        return subscriptionRepository.findById(query.subscriptionId())
                .map(Subscription::getPayments)
                .orElse(List.of());
    }
}
