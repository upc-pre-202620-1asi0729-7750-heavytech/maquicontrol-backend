package com.andesheavytech.maquicontrol.platform.subscription.application.acl;

import com.andesheavytech.maquicontrol.platform.subscription.application.queryservices.SubscriptionManagementQueryService;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.SubscriptionPlan;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.GetPlanByIdQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.ViewSubscriptionQuery;
import com.andesheavytech.maquicontrol.platform.subscription.interfaces.acl.SubscriptionContextFacade;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link SubscriptionContextFacade}.
 */
@Service
public class SubscriptionContextFacadeImpl implements SubscriptionContextFacade {

    private final SubscriptionManagementQueryService subscriptionManagementQueryService;

    public SubscriptionContextFacadeImpl(SubscriptionManagementQueryService subscriptionManagementQueryService) {
        this.subscriptionManagementQueryService = subscriptionManagementQueryService;
    }

    @Override
    public boolean hasActiveSubscription(Long ownerProfileId) {
        if (ownerProfileId == null || ownerProfileId <= 0) {
            return false;
        }
        return subscriptionManagementQueryService.handle(new ViewSubscriptionQuery(ownerProfileId))
                .filter(sub -> sub.isActive())
                .isPresent();
    }

    @Override
    public int fetchMachineryLimit(Long ownerProfileId) {
        if (ownerProfileId == null || ownerProfileId <= 0) {
            return 0;
        }
        return subscriptionManagementQueryService.handle(new ViewSubscriptionQuery(ownerProfileId))
                .filter(sub -> sub.isActive())
                .flatMap(sub -> subscriptionManagementQueryService.handle(new GetPlanByIdQuery(sub.getPlanId())))
                .map(SubscriptionPlan::getMachineryLimit)
                .orElse(0);
    }
}
