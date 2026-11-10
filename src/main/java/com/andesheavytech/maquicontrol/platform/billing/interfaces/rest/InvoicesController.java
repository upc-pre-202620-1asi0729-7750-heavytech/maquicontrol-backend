package com.andesheavytech.maquicontrol.platform.billing.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.billing.application.commandservices.BillingManagementCommandService;
import com.andesheavytech.maquicontrol.platform.billing.application.queryservices.BillingManagementQueryService;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.commands.CalculateRentalAmountCommand;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.commands.IssueInvoiceCommand;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewInvoiceByRentalIdQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewInvoiceQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewInvoicesByOwnerProfileIdQuery;
import com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.resources.CalculateInvoiceResource;
import com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.resources.InvoiceResource;
import com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.transform.BillingResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.acl.ProfilesContextFacade;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for managing electronic billing invoices.
 */
@RestController
@RequestMapping(value = "/api/v1/invoices", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Invoices", description = "Electronic invoicing endpoints integrated with SUNAT")
public class InvoicesController {

    private final BillingManagementCommandService billingManagementCommandService;
    private final BillingManagementQueryService billingManagementQueryService;
    private final ProfilesContextFacade profilesContextFacade;

    public InvoicesController(BillingManagementCommandService billingManagementCommandService,
                              BillingManagementQueryService billingManagementQueryService,
                              ProfilesContextFacade profilesContextFacade) {
        this.billingManagementCommandService = billingManagementCommandService;
        this.billingManagementQueryService = billingManagementQueryService;
        this.profilesContextFacade = profilesContextFacade;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FLEET_OWNER','FLEET_ADMINISTRATOR')")
    @Operation(summary = "Calculate invoice", description = "Calculates and creates a pending invoice for a rental.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Invoice calculated",
                    content = @Content(schema = @Schema(implementation = InvoiceResource.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Rental not found")
    })
    public ResponseEntity<?> calculateInvoice(@Valid @RequestBody CalculateInvoiceResource resource) {
        String currency = resource.currency() != null && !resource.currency().isBlank() ? resource.currency() : "PEN";
        var command = new CalculateRentalAmountCommand(resource.rentalId(), new Money(resource.amount(), currency));
        return ResponseEntityAssembler.toResponseEntityFromResult(
                billingManagementCommandService.handle(command),
                BillingResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    @PostMapping("/{invoiceId}/issue")
    @Operation(summary = "Issue electronic invoice to SUNAT", description = "Submits the invoice to SUNAT and generates series, correlative, and CDR.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoice issued to SUNAT",
                    content = @Content(schema = @Schema(implementation = InvoiceResource.class))),
            @ApiResponse(responseCode = "403", description = "Only the fleet owner can issue this invoice"),
            @ApiResponse(responseCode = "404", description = "Invoice not found"),
            @ApiResponse(responseCode = "422", description = "Invoice not in PENDING status")
    })
    public ResponseEntity<?> issueInvoice(@PathVariable Long invoiceId, Authentication authentication) {
        Long ownerProfileId = null;
        if (authentication != null && authentication.getName() != null) {
            ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        }
        var command = new IssueInvoiceCommand(invoiceId, ownerProfileId);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                billingManagementCommandService.handle(command),
                BillingResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Query invoices", description = "Filter by rentalId, or view the authenticated owner's invoices.")
    public ResponseEntity<List<InvoiceResource>> getInvoices(
            @RequestParam(required = false) Long rentalId,
            Authentication authentication) {
        if (rentalId != null && rentalId > 0) {
            var invoiceOpt = billingManagementQueryService.handle(new ViewInvoiceByRentalIdQuery(rentalId));
            var list = invoiceOpt.map(inv -> List.of(BillingResourceFromEntityAssembler.toResourceFromEntity(inv)))
                    .orElseGet(List::of);
            return ResponseEntity.ok(list);
        }
        if (authentication != null && authentication.getName() != null) {
            Long ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
            if (ownerProfileId != null && ownerProfileId > 0) {
                var list = billingManagementQueryService.handle(new ViewInvoicesByOwnerProfileIdQuery(ownerProfileId)).stream()
                        .map(BillingResourceFromEntityAssembler::toResourceFromEntity)
                        .toList();
                return ResponseEntity.ok(list);
            }
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{invoiceId}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<InvoiceResource> getInvoiceById(@PathVariable Long invoiceId) {
        return billingManagementQueryService.handle(new ViewInvoiceQuery(invoiceId))
                .map(BillingResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
