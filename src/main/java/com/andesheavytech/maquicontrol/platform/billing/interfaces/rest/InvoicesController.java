package com.andesheavytech.maquicontrol.platform.billing.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.billing.application.commandservices.BillingManagementCommandService;
import com.andesheavytech.maquicontrol.platform.billing.application.queryservices.BillingManagementQueryService;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.commands.IssueInvoiceCommand;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewInvoiceByRentalIdQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewInvoiceQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewInvoicesByOwnerProfileIdQuery;
import com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.resources.InvoiceResource;
import com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.resources.UpdateInvoiceStatusResource;
import com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.transform.BillingResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.acl.ProfilesContextFacade;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.acl.RentalContextFacade;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for electronic invoices. Invoices are calculated by a policy when an operation is
 * completed, so the API only lets the fleet owner issue them and lets the participants read them.
 */
@RestController
@RequestMapping(value = "/api/v1/invoices", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Invoices", description = "Electronic invoicing endpoints integrated with SUNAT")
public class InvoicesController {

    private final BillingManagementCommandService billingManagementCommandService;
    private final BillingManagementQueryService billingManagementQueryService;
    private final ProfilesContextFacade profilesContextFacade;
    private final RentalContextFacade rentalContextFacade;

    public InvoicesController(BillingManagementCommandService billingManagementCommandService,
                              BillingManagementQueryService billingManagementQueryService,
                              ProfilesContextFacade profilesContextFacade,
                              RentalContextFacade rentalContextFacade) {
        this.billingManagementCommandService = billingManagementCommandService;
        this.billingManagementQueryService = billingManagementQueryService;
        this.profilesContextFacade = profilesContextFacade;
        this.rentalContextFacade = rentalContextFacade;
    }

    @PatchMapping("/{invoiceId}/status")
    @PreAuthorize("hasAnyRole('FLEET_OWNER','FLEET_ADMINISTRATOR')")
    @Operation(summary = "Issue invoice", description = "With status ISSUED, submits the invoice to SUNAT and generates series, correlative and CDR.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoice issued to SUNAT",
                    content = @Content(schema = @Schema(implementation = InvoiceResource.class))),
            @ApiResponse(responseCode = "400", description = "Status other than ISSUED"),
            @ApiResponse(responseCode = "403", description = "Only the fleet owner can issue this invoice"),
            @ApiResponse(responseCode = "404", description = "Invoice not found"),
            @ApiResponse(responseCode = "422", description = "Invoice not in PENDING status")
    })
    public ResponseEntity<?> issueInvoice(@PathVariable Long invoiceId,
                                          @Valid @RequestBody UpdateInvoiceStatusResource resource,
                                          Authentication authentication) {
        if (!"ISSUED".equalsIgnoreCase(resource.status())) {
            return ResponseEntity.badRequest().build();
        }
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var command = new IssueInvoiceCommand(invoiceId, ownerProfileId);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                billingManagementCommandService.handle(command),
                BillingResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Query invoices", description = "Invoice of a rental (only for its participants), or the invoices of the signed-in fleet owner.")
    public ResponseEntity<List<InvoiceResource>> getInvoices(@RequestParam(required = false) Long rentalId,
                                                             Authentication authentication) {
        var profileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        if (rentalId != null) {
            if (!rentalContextFacade.isRentalParticipant(rentalId, profileId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            var list = billingManagementQueryService.handle(new ViewInvoiceByRentalIdQuery(rentalId))
                    .map(invoice -> List.of(BillingResourceFromEntityAssembler.toResourceFromEntity(invoice)))
                    .orElseGet(List::of);
            return ResponseEntity.ok(list);
        }
        if (profileId == null || profileId <= 0) {
            return ResponseEntity.ok(List.of());
        }
        var list = billingManagementQueryService.handle(new ViewInvoicesByOwnerProfileIdQuery(profileId)).stream()
                .map(BillingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{invoiceId}")
    @Operation(summary = "Get invoice by ID", description = "Only the contractor of the rental or the fleet owner can see it.")
    public ResponseEntity<InvoiceResource> getInvoiceById(@PathVariable Long invoiceId, Authentication authentication) {
        var invoice = billingManagementQueryService.handle(new ViewInvoiceQuery(invoiceId));
        if (invoice.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var profileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        if (!rentalContextFacade.isRentalParticipant(invoice.get().getRentalId(), profileId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(BillingResourceFromEntityAssembler.toResourceFromEntity(invoice.get()));
    }
}
