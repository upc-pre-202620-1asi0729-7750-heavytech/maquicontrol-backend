package com.andesheavytech.maquicontrol.platform.billing.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.billing.application.queryservices.BillingManagementQueryService;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.GetLatePenaltyByIdQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewLatePenaltiesQuery;
import com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.resources.LatePenaltyResource;
import com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.transform.BillingResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.acl.ProfilesContextFacade;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.acl.RentalContextFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only controller for late penalties. Penalties are applied by a policy when a rental is
 * completed after its agreed end date, so there is no endpoint to create them.
 */
@RestController
@RequestMapping(value = "/api/v1/late-penalties", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Late Penalties", description = "Late return penalty endpoints")
public class LatePenaltiesController {

    private final BillingManagementQueryService billingManagementQueryService;
    private final ProfilesContextFacade profilesContextFacade;
    private final RentalContextFacade rentalContextFacade;

    public LatePenaltiesController(BillingManagementQueryService billingManagementQueryService,
                                   ProfilesContextFacade profilesContextFacade,
                                   RentalContextFacade rentalContextFacade) {
        this.billingManagementQueryService = billingManagementQueryService;
        this.profilesContextFacade = profilesContextFacade;
        this.rentalContextFacade = rentalContextFacade;
    }

    @GetMapping
    @Operation(summary = "List late penalties of a rental", description = "Only the contractor of the rental or the fleet owner can see them.")
    public ResponseEntity<List<LatePenaltyResource>> getLatePenalties(@RequestParam Long rentalId,
                                                                      Authentication authentication) {
        var profileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        if (!rentalContextFacade.isRentalParticipant(rentalId, profileId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var list = billingManagementQueryService.handle(new ViewLatePenaltiesQuery(rentalId)).stream()
                .map(BillingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{penaltyId}")
    @Operation(summary = "Get late penalty by ID", description = "Only the contractor of the rental or the fleet owner can see it.")
    public ResponseEntity<LatePenaltyResource> getLatePenaltyById(@PathVariable Long penaltyId, Authentication authentication) {
        var penalty = billingManagementQueryService.handle(new GetLatePenaltyByIdQuery(penaltyId));
        if (penalty.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var profileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        if (!rentalContextFacade.isRentalParticipant(penalty.get().getRentalId(), profileId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(BillingResourceFromEntityAssembler.toResourceFromEntity(penalty.get()));
    }
}
