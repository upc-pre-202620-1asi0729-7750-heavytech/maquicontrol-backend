package com.andesheavytech.maquicontrol.platform.subscription.application.queryservices;

import com.andesheavytech.maquicontrol.platform.subscription.domain.model.aggregates.Subscription;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.Payment;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.SubscriptionPlan;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.GetPlanByCodeQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.GetPlanByIdQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.GetSubscriptionByIdQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.ViewPaymentsQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.ViewPlansQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.ViewSubscriptionQuery;

import java.util.List;
import java.util.Optional;

/**
 * Query service port for Subscription Management.
 */
public interface SubscriptionManagementQueryService {

    List<SubscriptionPlan> handle(ViewPlansQuery query);

    Optional<SubscriptionPlan> handle(GetPlanByIdQuery query);

    Optional<SubscriptionPlan> handle(GetPlanByCodeQuery query);

    Optional<Subscription> handle(ViewSubscriptionQuery query);

    Optional<Subscription> handle(GetSubscriptionByIdQuery query);

    List<Payment> handle(ViewPaymentsQuery query);
}
