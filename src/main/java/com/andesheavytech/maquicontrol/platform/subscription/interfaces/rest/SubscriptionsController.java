package com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.profiles.interfaces.acl.ProfilesContextFacade;
import com.andesheavytech.maquicontrol.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import com.andesheavytech.maquicontrol.platform.subscription.application.commandservices.SubscriptionManagementCommandService;
import com.andesheavytech.maquicontrol.platform.subscription.application.queryservices.SubscriptionManagementQueryService;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands.CancelSubscriptionCommand;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands.ConfirmCheckoutCommand;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands.SubscribeToPlanCommand;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.GetSubscriptionByIdQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.ViewPaymentsQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.ViewSubscriptionQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PaymentStatus;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PlanCode;
import com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.resources.CancelSubscriptionResource;
import com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.resources.ConfirmCheckoutResource;
import com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.resources.PaymentResource;
import com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.resources.SubscribeToPlanResource;
import com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.resources.SubscriptionResource;
import com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for subscription lifecycle and checkout management.
 */
@RestController
@RequestMapping(value = "/api/v1/subscriptions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Subscriptions", description = "Plan subscriptions and payment management endpoints")
public class SubscriptionsController {

    private final SubscriptionManagementCommandService subscriptionManagementCommandService;
    private final SubscriptionManagementQueryService subscriptionManagementQueryService;
    private final ProfilesContextFacade profilesContextFacade;

    public SubscriptionsController(SubscriptionManagementCommandService subscriptionManagementCommandService,
                                   SubscriptionManagementQueryService subscriptionManagementQueryService,
                                   ProfilesContextFacade profilesContextFacade) {
        this.subscriptionManagementCommandService = subscriptionManagementCommandService;
        this.subscriptionManagementQueryService = subscriptionManagementQueryService;
        this.profilesContextFacade = profilesContextFacade;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FLEET_OWNER','FLEET_ADMINISTRATOR')")
    @Operation(summary = "Subscribe to plan", description = "Fleet owner selects a plan and creates a pending subscription.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Subscription requested",
                    content = @Content(schema = @Schema(implementation = SubscriptionResource.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Plan not found"),
            @ApiResponse(responseCode = "409", description = "Owner already has an active subscription")
    })
    public ResponseEntity<?> subscribeToPlan(@Valid @RequestBody SubscribeToPlanResource resource,
                                             Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        PlanCode code;
        try {
            code = PlanCode.valueOf(resource.planCode().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        var command = new SubscribeToPlanCommand(ownerProfileId, code);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                subscriptionManagementCommandService.handle(command),
                SubscriptionResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    @PostMapping("/{subscriptionId}/payments")
    @Operation(summary = "Confirm checkout", description = "Registers payment from Mercado Pago and activates subscription if approved.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment recorded and subscription activated",
                    content = @Content(schema = @Schema(implementation = SubscriptionResource.class))),
            @ApiResponse(responseCode = "404", description = "Subscription not found"),
            @ApiResponse(responseCode = "422", description = "Subscription cancelled")
    })
    public ResponseEntity<?> confirmCheckout(@PathVariable Long subscriptionId,
                                            @Valid @RequestBody ConfirmCheckoutResource resource) {
        PaymentStatus status = PaymentStatus.APPROVED;
        if (resource.status() != null) {
            try {
                status = PaymentStatus.valueOf(resource.status().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        var command = new ConfirmCheckoutCommand(
                subscriptionId, resource.providerPaymentId(), resource.amount(), resource.currency(), status);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                subscriptionManagementCommandService.handle(command),
                SubscriptionResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    @PatchMapping("/{subscriptionId}/status")
    @PreAuthorize("hasAnyRole('FLEET_OWNER','FLEET_ADMINISTRATOR')")
    @Operation(summary = "Cancel subscription", description = "Fleet owner cancels an active subscription.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subscription cancelled",
                    content = @Content(schema = @Schema(implementation = SubscriptionResource.class))),
            @ApiResponse(responseCode = "403", description = "Subscription belongs to another user"),
            @ApiResponse(responseCode = "404", description = "Subscription not found"),
            @ApiResponse(responseCode = "422", description = "Subscription not in ACTIVE status")
    })
    public ResponseEntity<?> cancelSubscription(@PathVariable Long subscriptionId,
                                                @Valid @RequestBody CancelSubscriptionResource resource,
                                                Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        if (!"CANCELLED".equalsIgnoreCase(resource.status())) {
            return ResponseEntity.badRequest().build();
        }
        var command = new CancelSubscriptionCommand(subscriptionId, ownerProfileId, resource.reason());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                subscriptionManagementCommandService.handle(command),
                SubscriptionResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FLEET_OWNER','FLEET_ADMINISTRATOR')")
    @Operation(summary = "View my subscription", description = "Retrieves the current subscription for the signed-in fleet owner.")
    public ResponseEntity<SubscriptionResource> getMySubscription(Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        return subscriptionManagementQueryService.handle(new ViewSubscriptionQuery(ownerProfileId))
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{subscriptionId}")
    @Operation(summary = "Get subscription by ID")
    public ResponseEntity<SubscriptionResource> getSubscriptionById(@PathVariable Long subscriptionId) {
        return subscriptionManagementQueryService.handle(new GetSubscriptionByIdQuery(subscriptionId))
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{subscriptionId}/payments")
    @Operation(summary = "View payment history of a subscription")
    public ResponseEntity<List<PaymentResource>> getPayments(@PathVariable Long subscriptionId) {
        var payments = subscriptionManagementQueryService.handle(new ViewPaymentsQuery(subscriptionId)).stream()
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(payments);
    }
}
