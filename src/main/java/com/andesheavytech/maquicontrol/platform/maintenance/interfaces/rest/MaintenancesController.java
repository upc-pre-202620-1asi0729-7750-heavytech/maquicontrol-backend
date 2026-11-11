package com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.fleet.interfaces.acl.FleetContextFacade;
import com.andesheavytech.maquicontrol.platform.maintenance.application.commandservices.MaintenanceManagementCommandService;
import com.andesheavytech.maquicontrol.platform.maintenance.application.queryservices.MaintenanceManagementQueryService;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.aggregates.Maintenance;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.CancelMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.CompleteMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.ScheduleMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.StartMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries.CheckMaintenanceDetailsQuery;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries.GetMaintenancesByOwnerProfileIdQuery;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries.ViewMaintenanceHistoryQuery;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries.ViewMaintenanceScheduleQuery;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceStatus;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceType;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources.MaintenanceResource;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources.ScheduleMaintenanceResource;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources.UpdateMaintenanceStatusResource;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.transform.MaintenanceEnumAssembler;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.transform.MaintenanceResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.acl.ProfilesContextFacade;
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

import java.util.List;
import java.util.Objects;

/**
 * Maintenance endpoints. Owners schedule, start, complete and cancel maintenance; owners and
 * contractors with a rental in progress report breakdowns.
 */
@RestController
@RequestMapping(value = "/api/v1/maintenances", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Maintenances", description = "Maintenance scheduling endpoints")
public class MaintenancesController {

    private static final String OWNER_ROLES = "hasAnyRole('FLEET_OWNER','FLEET_ADMINISTRATOR')";

    private final MaintenanceManagementCommandService maintenanceManagementCommandService;
    private final MaintenanceManagementQueryService maintenanceManagementQueryService;
    private final ProfilesContextFacade profilesContextFacade;
    private final FleetContextFacade fleetContextFacade;

    public MaintenancesController(MaintenanceManagementCommandService maintenanceManagementCommandService,
                                  MaintenanceManagementQueryService maintenanceManagementQueryService,
                                  ProfilesContextFacade profilesContextFacade,
                                  FleetContextFacade fleetContextFacade) {
        this.maintenanceManagementCommandService = maintenanceManagementCommandService;
        this.maintenanceManagementQueryService = maintenanceManagementQueryService;
        this.profilesContextFacade = profilesContextFacade;
        this.fleetContextFacade = fleetContextFacade;
    }

    @PostMapping
    @PreAuthorize(OWNER_ROLES)
    @Operation(summary = "Schedule maintenance", description = "The owner schedules a maintenance for one of their machines.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Maintenance scheduled",
                    content = @Content(schema = @Schema(implementation = MaintenanceResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data or date in the past"),
            @ApiResponse(responseCode = "403", description = "Machinery of another owner"),
            @ApiResponse(responseCode = "404", description = "Machinery not found")
    })
    public ResponseEntity<?> scheduleMaintenance(@Valid @RequestBody ScheduleMaintenanceResource resource,
                                                 Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var command = new ScheduleMaintenanceCommand(resource.machineryId(), ownerProfileId,
                MaintenanceEnumAssembler.toEnum(MaintenanceType.class, resource.type(), "type"),
                resource.description(), resource.scheduledDate(), resource.technicianName());
        return ResponseEntityAssembler.toResponseEntityFromResult(maintenanceManagementCommandService.handle(command),
                MaintenanceResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PatchMapping("/{maintenanceId}/status")
    @PreAuthorize(OWNER_ROLES)
    @Operation(summary = "Update maintenance status",
            description = "IN_PROGRESS starts it (the machine goes IN_MAINTENANCE), COMPLETED completes it with an optional cost (the machine goes back to AVAILABLE) and CANCELLED cancels a scheduled one.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated",
                    content = @Content(schema = @Schema(implementation = MaintenanceResource.class))),
            @ApiResponse(responseCode = "403", description = "Maintenance of another owner"),
            @ApiResponse(responseCode = "404", description = "Maintenance not found"),
            @ApiResponse(responseCode = "422", description = "Transition not allowed or machinery reserved or rented")
    })
    public ResponseEntity<?> updateMaintenanceStatus(@PathVariable Long maintenanceId,
                                                     @Valid @RequestBody UpdateMaintenanceStatusResource resource,
                                                     Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var status = MaintenanceEnumAssembler.toEnum(MaintenanceStatus.class, resource.status(), "status");
        Result<Maintenance, ApplicationError> result = switch (status) {
            case IN_PROGRESS -> maintenanceManagementCommandService.handle(new StartMaintenanceCommand(maintenanceId, ownerProfileId));
            case COMPLETED -> maintenanceManagementCommandService.handle(new CompleteMaintenanceCommand(maintenanceId, ownerProfileId,
                    resource.cost() == null ? null : resource.cost().amount(),
                    resource.cost() == null ? null : resource.cost().currency()));
            case CANCELLED -> maintenanceManagementCommandService.handle(new CancelMaintenanceCommand(maintenanceId, ownerProfileId));
            case SCHEDULED -> Result.failure(ApplicationError.validationError("status", "a maintenance cannot go back to SCHEDULED"));
        };
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                MaintenanceResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize(OWNER_ROLES)
    @Operation(summary = "View my fleet maintenances", description = "Every maintenance of the signed-in owner's machines, optionally for one machine.")
    public ResponseEntity<List<MaintenanceResource>> getMaintenances(@RequestParam(required = false) Long machineryId,
                                                                     Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        return ResponseEntity.ok(toResources(maintenanceManagementQueryService.handle(
                new GetMaintenancesByOwnerProfileIdQuery(ownerProfileId, machineryId))));
    }

    @GetMapping("/pending")
    @PreAuthorize(OWNER_ROLES)
    @Operation(summary = "View pending maintenance", description = "Scheduled and in-progress maintenances of the signed-in owner's machines.")
    public ResponseEntity<List<MaintenanceResource>> getPendingMaintenance(Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        return ResponseEntity.ok(toResources(maintenanceManagementQueryService.handle(new ViewMaintenanceScheduleQuery(ownerProfileId))));
    }

    @GetMapping("/history")
    @Operation(summary = "View maintenance history", description = "Completed maintenances of a machine.")
    public ResponseEntity<List<MaintenanceResource>> getMaintenanceHistory(@RequestParam Long machineryId) {
        return ResponseEntity.ok(toResources(maintenanceManagementQueryService.handle(new ViewMaintenanceHistoryQuery(machineryId))));
    }

    @GetMapping("/{maintenanceId}")
    @Operation(summary = "Check maintenance details")
    public ResponseEntity<?> getMaintenanceById(@PathVariable Long maintenanceId, Authentication authentication) {
        var profileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var maintenance = maintenanceManagementQueryService.handle(new CheckMaintenanceDetailsQuery(maintenanceId));
        if (maintenance.isEmpty()) {
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                    ApplicationError.notFound("Maintenance", String.valueOf(maintenanceId)));
        }
        if (!Objects.equals(fleetContextFacade.fetchOwnerProfileId(maintenance.get().getMachineryId()), profileId)) {
            return ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                    ApplicationError.forbidden("maintenance", "only the machinery owner can see this maintenance"));
        }
        return ResponseEntity.ok(MaintenanceResourceFromEntityAssembler.toResourceFromEntity(maintenance.get()));
    }

    private static List<MaintenanceResource> toResources(List<Maintenance> maintenances) {
        return maintenances.stream().map(MaintenanceResourceFromEntityAssembler::toResourceFromEntity).toList();
    }
}
