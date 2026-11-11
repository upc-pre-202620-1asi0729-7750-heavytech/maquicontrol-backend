package com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.subscription.application.queryservices.SubscriptionManagementQueryService;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.GetPlanByIdQuery;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.queries.ViewPlansQuery;
import com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.resources.PlanResource;
import com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.transform.SubscriptionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for subscription plans catalog.
 */
@RestController
@RequestMapping(value = "/api/v1/plans", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Subscription Plans", description = "Available subscription plans catalog endpoints")
public class PlansController {

    private final SubscriptionManagementQueryService subscriptionManagementQueryService;

    public PlansController(SubscriptionManagementQueryService subscriptionManagementQueryService) {
        this.subscriptionManagementQueryService = subscriptionManagementQueryService;
    }

    @GetMapping
    @Operation(summary = "View plan catalog", description = "Lists all available subscription plans (e.g. Essential and Pro).")
    public ResponseEntity<List<PlanResource>> getPlans() {
        var plans = subscriptionManagementQueryService.handle(new ViewPlansQuery()).stream()
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{planId}")
    @Operation(summary = "Get plan by ID")
    public ResponseEntity<PlanResource> getPlanById(@PathVariable Long planId) {
        return subscriptionManagementQueryService.handle(new GetPlanByIdQuery(planId))
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
