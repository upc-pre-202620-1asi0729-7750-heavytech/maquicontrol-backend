package com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.fleet.interfaces.acl.FleetContextFacade;
import com.andesheavytech.maquicontrol.platform.maintenance.application.commandservices.MaintenanceManagementCommandService;
import com.andesheavytech.maquicontrol.platform.maintenance.application.queryservices.MaintenanceManagementQueryService;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.ReportBreakdownCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries.ViewBreakdownReportsQuery;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.BreakdownSeverity;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources.BreakdownReportResource;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources.MaintenanceResource;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources.ReportBreakdownResource;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.transform.MaintenanceEnumAssembler;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.transform.MaintenanceResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.acl.ProfilesContextFacade;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * Breakdown reports. A report is added to the pending corrective maintenance of the machine, or
 * a new corrective maintenance is scheduled for it.
 */
@RestController
@RequestMapping(value = "/api/v1/breakdown-reports", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Breakdown Reports", description = "Breakdown reporting endpoints")
public class BreakdownReportsController {

    private static final String OWNER_ROLES = "hasAnyRole('FLEET_OWNER','FLEET_ADMINISTRATOR')";

    private final MaintenanceManagementCommandService maintenanceManagementCommandService;
    private final MaintenanceManagementQueryService maintenanceManagementQueryService;
    private final ProfilesContextFacade profilesContextFacade;
    private final FleetContextFacade fleetContextFacade;

    public BreakdownReportsController(MaintenanceManagementCommandService maintenanceManagementCommandService,
                                      MaintenanceManagementQueryService maintenanceManagementQueryService,
                                      ProfilesContextFacade profilesContextFacade,
                                      FleetContextFacade fleetContextFacade) {
        this.maintenanceManagementCommandService = maintenanceManagementCommandService;
        this.maintenanceManagementQueryService = maintenanceManagementQueryService;
        this.profilesContextFacade = profilesContextFacade;
        this.fleetContextFacade = fleetContextFacade;
    }

    @PostMapping
    @Operation(summary = "Report breakdown",
            description = "Reports a breakdown on a machine. It is added to its pending corrective maintenance, or a new corrective maintenance is scheduled.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Breakdown reported",
                    content = @Content(schema = @Schema(implementation = MaintenanceResource.class))),
            @ApiResponse(responseCode = "403", description = "Not the owner nor a contractor with a rental in progress"),
            @ApiResponse(responseCode = "404", description = "Machinery not found")
    })
    public ResponseEntity<?> reportBreakdown(@Valid @RequestBody ReportBreakdownResource resource,
                                             Authentication authentication) {
        var reporterProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var command = new ReportBreakdownCommand(resource.machineryId(), reporterProfileId, resource.description(),
                MaintenanceEnumAssembler.toEnum(BreakdownSeverity.class, resource.severity(), "severity"));
        return ResponseEntityAssembler.toResponseEntityFromResult(maintenanceManagementCommandService.handle(command),
                MaintenanceResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize(OWNER_ROLES)
    @Operation(summary = "View breakdown reports", description = "Breakdown reports of a machine, only for its owner.")
    public ResponseEntity<List<BreakdownReportResource>> getBreakdownReports(@RequestParam Long machineryId,
                                                                             Authentication authentication) {
        var profileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        if (!Objects.equals(fleetContextFacade.fetchOwnerProfileId(machineryId), profileId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var reports = maintenanceManagementQueryService.handle(new ViewBreakdownReportsQuery(machineryId)).stream()
                .flatMap(maintenance -> maintenance.getBreakdownReports().stream())
                .map(MaintenanceResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(reports);
    }
}
