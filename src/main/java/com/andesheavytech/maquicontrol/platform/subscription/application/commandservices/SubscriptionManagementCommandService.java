package com.andesheavytech.maquicontrol.platform.subscription.application.commandservices;

import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.aggregates.Subscription;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands.ActivateSubscriptionCommand;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands.CancelSubscriptionCommand;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands.ConfirmCheckoutCommand;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands.SubscribeToPlanCommand;

/**
 * Command service port for Subscription Management.
 */
public interface SubscriptionManagementCommandService {

    Result<Subscription, ApplicationError> handle(SubscribeToPlanCommand command);

    Result<Subscription, ApplicationError> handle(ConfirmCheckoutCommand command);

    Result<Subscription, ApplicationError> handle(ActivateSubscriptionCommand command);

    Result<Subscription, ApplicationError> handle(CancelSubscriptionCommand command);
}
