package com.andesheavytech.maquicontrol.platform.subscription.application.internal.commandservices;

import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import com.andesheavytech.maquicontrol.platform.subscription.application.commandservices.SubscriptionManagementCommandService;
import com.andesheavytech.maquicontrol.platform.subscription.application.internal.outboundservices.acl.MercadoPagoService;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.aggregates.Subscription;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands.ActivateSubscriptionCommand;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands.CancelSubscriptionCommand;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands.ConfirmCheckoutCommand;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands.SubscribeToPlanCommand;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PaymentStatus;
import com.andesheavytech.maquicontrol.platform.subscription.domain.repositories.SubscriptionPlanRepository;
import com.andesheavytech.maquicontrol.platform.subscription.domain.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Implementation of {@link SubscriptionManagementCommandService}.
 */
@Service
public class SubscriptionManagementCommandServiceImpl implements SubscriptionManagementCommandService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final MercadoPagoService mercadoPagoService;

    public SubscriptionManagementCommandServiceImpl(SubscriptionRepository subscriptionRepository,
                                                    SubscriptionPlanRepository subscriptionPlanRepository,
                                                    MercadoPagoService mercadoPagoService) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.mercadoPagoService = mercadoPagoService;
    }

    @Override
    @Transactional
    public Result<Subscription, ApplicationError> handle(SubscribeToPlanCommand command) {
        if (command.ownerProfileId() == null || command.ownerProfileId() <= 0) {
            return Result.failure(ApplicationError.businessRuleViolation("profile required", "create your profile first"));
        }
        if (subscriptionRepository.existsActiveByOwnerProfileId(command.ownerProfileId())) {
            return Result.failure(ApplicationError.conflict("Subscription", "owner already has an active subscription"));
        }
        var planOpt = subscriptionPlanRepository.findByCode(command.planCode());
        if (planOpt.isEmpty()) {
            return Result.failure(ApplicationError.notFound("SubscriptionPlan", command.planCode().name()));
        }
        try {
            var subscription = Subscription.subscribe(command.ownerProfileId(), planOpt.get().getId());
            return Result.success(subscriptionRepository.save(subscription));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("subscription", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Subscription, ApplicationError> handle(ConfirmCheckoutCommand command) {
        var found = subscriptionRepository.findById(command.subscriptionId());
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Subscription", String.valueOf(command.subscriptionId())));
        }
        var subscription = found.get();
        var planOpt = subscriptionPlanRepository.findById(subscription.getPlanId());
        Money amount;
        if (command.amount() != null && command.currency() != null) {
            amount = new Money(command.amount(), command.currency());
        } else if (planOpt.isPresent()) {
            amount = planOpt.get().getPrice();
        } else {
            amount = new Money(BigDecimal.ZERO, "PEN");
        }

        try {
            var payment = mercadoPagoService.processPayment(
                    command.subscriptionId(), command.providerPaymentId(), amount, command.status());
            subscription.confirmPayment(payment);

            // Policy: when payment is approved, activate the subscription
            if (payment.isApproved() && subscription.isPendingPayment()) {
                subscription.activate();
            }

            return Result.success(subscriptionRepository.save(subscription));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("subscription status", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("payment", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Subscription, ApplicationError> handle(ActivateSubscriptionCommand command) {
        var found = subscriptionRepository.findById(command.subscriptionId());
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Subscription", String.valueOf(command.subscriptionId())));
        }
        var subscription = found.get();
        try {
            subscription.activate();
            return Result.success(subscriptionRepository.save(subscription));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("subscription status", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Subscription, ApplicationError> handle(CancelSubscriptionCommand command) {
        var found = subscriptionRepository.findById(command.subscriptionId());
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Subscription", String.valueOf(command.subscriptionId())));
        }
        var subscription = found.get();
        if (command.ownerProfileId() != null && !subscription.getOwnerProfileId().equals(command.ownerProfileId())) {
            return Result.failure(ApplicationError.forbidden("subscription", "only the owner can cancel their subscription"));
        }
        try {
            subscription.cancel(command.reason());
            return Result.success(subscriptionRepository.save(subscription));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("subscription status", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("reason", e.getMessage()));
        }
    }
}
