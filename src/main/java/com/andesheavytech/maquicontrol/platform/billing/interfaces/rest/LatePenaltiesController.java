package com.andesheavytech.maquicontrol.platform.billing.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.billing.application.commandservices.BillingManagementCommandService;
import com.andesheavytech.maquicontrol.platform.billing.application.queryservices.BillingManagementQueryService;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.commands.ApplyLatePenaltyCommand;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.GetLatePenaltyByIdQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewLatePenaltiesQuery;
import com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.resources.ApplyLatePenaltyResource;
import com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.resources.LatePenaltyResource;
import com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.transform.BillingResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import com.andesheavytech.maquicontrol.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for managing rental late penalty surcharges.
 */
@RestController
@RequestMapping(value = "/api/v1/late-penalties", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Late Penalties", description = "Late return penalty surcharge management endpoints")
public class LatePenaltiesController {

    private final BillingManagementCommandService billingManagementCommandService;
    private final BillingManagementQueryService billingManagementQueryService;

    public LatePenaltiesController(BillingManagementCommandService billingManagementCommandService,
                                   BillingManagementQueryService billingManagementQueryService) {
        this.billingManagementCommandService = billingManagementCommandService;
        this.billingManagementQueryService = billingManagementQueryService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FLEET_OWNER','FLEET_ADMINISTRATOR')")
    @Operation(summary = "Apply late penalty", description = "Applies a financial penalty surcharge to a rental returned after its agreed end date.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Late penalty applied",
                    content = @Content(schema = @Schema(implementation = LatePenaltyResource.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    public ResponseEntity<?> applyLatePenalty(@Valid @RequestBody ApplyLatePenaltyResource resource) {
        String currency = resource.currency() != null && !resource.currency().isBlank() ? resource.currency() : "PEN";
        var command = new ApplyLatePenaltyCommand(resource.rentalId(), new Money(resource.amount(), currency), resource.reason());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                billingManagementCommandService.handle(command),
                BillingResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List late penalties", description = "List all late penalties or filter by rental ID.")
    public ResponseEntity<List<LatePenaltyResource>> getLatePenalties(@RequestParam(required = false) Long rentalId) {
        var list = billingManagementQueryService.handle(new ViewLatePenaltiesQuery(rentalId)).stream()
                .map(BillingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{penaltyId}")
    @Operation(summary = "Get late penalty by ID")
    public ResponseEntity<LatePenaltyResource> getLatePenaltyById(@PathVariable Long penaltyId) {
        return billingManagementQueryService.handle(new GetLatePenaltyByIdQuery(penaltyId))
                .map(BillingResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
