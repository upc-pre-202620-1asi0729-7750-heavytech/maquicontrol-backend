package com.andesheavytech.maquicontrol.platform.rental.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.profiles.interfaces.acl.ProfilesContextFacade;
import com.andesheavytech.maquicontrol.platform.rental.application.commandservices.RentalManagementCommandService;
import com.andesheavytech.maquicontrol.platform.rental.application.queryservices.RentalManagementQueryService;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates.Rental;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.CancelReservationCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.ConfirmReservationCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.RejectReservationCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.RequestRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.CheckMachineryAvailabilityQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalByIdQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.ViewFleetRentalsQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.ViewMyReservationsQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.ViewPendingRequestsQuery;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.acl.RentalContextFacade;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.resources.MachineryAvailabilityResource;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.resources.RentalResource;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.resources.RequestRentalResource;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.resources.UpdateRentalStatusResource;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.transform.RentalResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.transform.RentalStatusCommandFromResourceAssembler;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import com.andesheavytech.maquicontrol.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import com.andesheavytech.maquicontrol.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Rental endpoints. Contractors request and cancel; owners confirm, reject or cancel.
 */
@RestController
@RequestMapping(value = "/api/v1/rentals", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Rentals", description = "Rental request and lifecycle endpoints")
public class RentalsController {

    private final RentalManagementCommandService rentalManagementCommandService;
    private final RentalManagementQueryService rentalManagementQueryService;
    private final ProfilesContextFacade profilesContextFacade;
    private final RentalContextFacade rentalContextFacade;

    public RentalsController(RentalManagementCommandService rentalManagementCommandService,
                             RentalManagementQueryService rentalManagementQueryService,
                             ProfilesContextFacade profilesContextFacade,
                             RentalContextFacade rentalContextFacade) {
        this.rentalManagementCommandService = rentalManagementCommandService;
        this.rentalManagementQueryService = rentalManagementQueryService;
        this.profilesContextFacade = profilesContextFacade;
        this.rentalContextFacade = rentalContextFacade;
    }

    @PostMapping
    @PreAuthorize("hasRole('CONTRACTOR')")
    @Operation(summary = "Request rental", description = "The signed-in contractor requests a machine for some dates.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rental requested",
                    content = @Content(schema = @Schema(implementation = RentalResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid dates"),
            @ApiResponse(responseCode = "404", description = "Machinery not found"),
            @ApiResponse(responseCode = "409", description = "Dates already taken"),
            @ApiResponse(responseCode = "422", description = "No profile, own machinery, or machinery not rentable")
    })
    public ResponseEntity<?> requestRental(@Valid @RequestBody RequestRentalResource resource, Authentication authentication) {
        var contractorProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var command = new RequestRentalCommand(resource.machineryId(), contractorProfileId, resource.startDate(), resource.endDate());
        return ResponseEntityAssembler.toResponseEntityFromResult(rentalManagementCommandService.handle(command),
                RentalResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PatchMapping("/{rentalId}/status")
    @Operation(summary = "Update rental status",
            description = "CONFIRMED or REJECTED (owner, reason required to reject) and CANCELLED (contractor or owner, reason required).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated",
                    content = @Content(schema = @Schema(implementation = RentalResource.class))),
            @ApiResponse(responseCode = "403", description = "Not a participant of the rental"),
            @ApiResponse(responseCode = "404", description = "Rental not found"),
            @ApiResponse(responseCode = "409", description = "Dates taken by another confirmed rental"),
            @ApiResponse(responseCode = "422", description = "Transition not allowed from the current status")
    })
    public ResponseEntity<?> updateRentalStatus(@PathVariable Long rentalId,
                                                @Valid @RequestBody UpdateRentalStatusResource resource,
                                                Authentication authentication) {
        var profileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        Result<Rental, ApplicationError> result = switch (RentalStatusCommandFromResourceAssembler.toCommandFromResource(rentalId, profileId, resource)) {
            case ConfirmReservationCommand command -> rentalManagementCommandService.handle(command);
            case RejectReservationCommand command -> rentalManagementCommandService.handle(command);
            case CancelReservationCommand command -> rentalManagementCommandService.handle(command);
            default -> throw new IllegalArgumentException("unsupported status");
        };
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                RentalResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @GetMapping("/my-reservations")
    @Operation(summary = "View my reservations", description = "Rentals requested by the signed-in contractor.")
    public ResponseEntity<List<RentalResource>> getMyReservations(Authentication authentication) {
        var contractorProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var rentals = rentalManagementQueryService.handle(new ViewMyReservationsQuery(contractorProfileId)).stream()
                .map(RentalResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/pending-requests")
    @PreAuthorize("hasAnyRole('FLEET_OWNER','FLEET_ADMINISTRATOR')")
    @Operation(summary = "View pending rental requests", description = "Requests waiting for the signed-in owner's answer.")
    public ResponseEntity<List<RentalResource>> getPendingRequests(Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var rentals = rentalManagementQueryService.handle(new ViewPendingRequestsQuery(ownerProfileId)).stream()
                .map(RentalResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/fleet-rentals")
    @PreAuthorize("hasAnyRole('FLEET_OWNER','FLEET_ADMINISTRATOR')")
    @Operation(summary = "View fleet rentals", description = "Every rental of the signed-in owner's machinery, in any status.")
    public ResponseEntity<List<RentalResource>> getFleetRentals(Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var rentals = rentalManagementQueryService.handle(new ViewFleetRentalsQuery(ownerProfileId)).stream()
                .map(RentalResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/availability")
    @Operation(summary = "Check machinery availability", description = "Whether a machine is free for the given dates.")
    public ResponseEntity<?> checkAvailability(@RequestParam Long machineryId,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        var result = rentalManagementQueryService.handle(new CheckMachineryAvailabilityQuery(machineryId, startDate, endDate));
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                available -> new MachineryAvailabilityResource(machineryId, startDate, endDate, available), HttpStatus.OK);
    }

    @GetMapping("/{rentalId}")
    @Operation(summary = "Get rental by id", description = "Only the contractor of the rental or the owner of the machinery can see it.")
    public ResponseEntity<?> getRentalById(@PathVariable Long rentalId, Authentication authentication) {
        var profileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        if (rentalContextFacade.existsRental(rentalId) && !rentalContextFacade.isRentalParticipant(rentalId, profileId)) {
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                    ApplicationError.forbidden("rental", "only the contractor or the machinery owner can see this rental"));
        }
        return rentalManagementQueryService.handle(new GetRentalByIdQuery(rentalId))
                .<ResponseEntity<?>>map(rental -> ResponseEntity.ok(RentalResourceFromEntityAssembler.toResourceFromEntity(rental)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                        ApplicationError.notFound("Rental", String.valueOf(rentalId))));
    }
}
