package com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.fleet.application.commandservices.FleetManagementCommandService;
import com.andesheavytech.maquicontrol.platform.fleet.application.queryservices.FleetManagementQueryService;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.DeleteMachineryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.queries.GetMachineryByIdQuery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.queries.GetMachineryByOwnerProfileIdQuery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.queries.ViewMachineryCatalogQuery;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources.ChangeMachineryStatusResource;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources.MachineryResource;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources.PublishMachineryResource;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources.UpdateMachineryResource;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.transform.ChangeMachineryStatusCommandFromResourceAssembler;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.transform.MachineryResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.transform.PublishMachineryCommandFromResourceAssembler;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.transform.UpdateMachineryCommandFromResourceAssembler;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.acl.ProfilesContextFacade;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.interfaces.rest.resources.MessageResource;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Machinery endpoints. Owners publish and manage their machines; every signed-in user can browse them.
 */
@RestController
@RequestMapping(value = "/api/v1/machinery", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Machinery", description = "Machinery catalog and owner management endpoints")
public class MachineryController {

    private static final String OWNER_ROLES = "hasAnyRole('FLEET_OWNER','FLEET_ADMINISTRATOR')";

    private final FleetManagementCommandService fleetManagementCommandService;
    private final FleetManagementQueryService fleetManagementQueryService;
    private final ProfilesContextFacade profilesContextFacade;

    public MachineryController(FleetManagementCommandService fleetManagementCommandService,
                               FleetManagementQueryService fleetManagementQueryService,
                               ProfilesContextFacade profilesContextFacade) {
        this.fleetManagementCommandService = fleetManagementCommandService;
        this.fleetManagementQueryService = fleetManagementQueryService;
        this.profilesContextFacade = profilesContextFacade;
    }

    @PostMapping
    @PreAuthorize(OWNER_ROLES)
    @Operation(summary = "Publish machinery", description = "Publishes a machine owned by the signed-in profile.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Machinery published",
                    content = @Content(schema = @Schema(implementation = MachineryResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "422", description = "The account has no profile yet")
    })
    public ResponseEntity<?> publishMachinery(@Valid @RequestBody PublishMachineryResource resource,
                                              Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var command = PublishMachineryCommandFromResourceAssembler.toCommandFromResource(ownerProfileId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(fleetManagementCommandService.handle(command),
                MachineryResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{machineryId}")
    @PreAuthorize(OWNER_ROLES)
    @Operation(summary = "Update machinery")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Machinery updated",
                    content = @Content(schema = @Schema(implementation = MachineryResource.class))),
            @ApiResponse(responseCode = "403", description = "Machinery of another owner"),
            @ApiResponse(responseCode = "404", description = "Machinery or category not found")
    })
    public ResponseEntity<?> updateMachinery(@PathVariable Long machineryId,
                                             @Valid @RequestBody UpdateMachineryResource resource,
                                             Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var command = UpdateMachineryCommandFromResourceAssembler.toCommandFromResource(machineryId, ownerProfileId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(fleetManagementCommandService.handle(command),
                MachineryResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @PatchMapping("/{machineryId}/status")
    @PreAuthorize(OWNER_ROLES)
    @Operation(summary = "Change machinery status",
            description = "The owner can set AVAILABLE, IN_MAINTENANCE or OUT_OF_SERVICE while the machine is not reserved or rented.")
    public ResponseEntity<?> changeMachineryStatus(@PathVariable Long machineryId,
                                                   @Valid @RequestBody ChangeMachineryStatusResource resource,
                                                   Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var command = ChangeMachineryStatusCommandFromResourceAssembler.toCommandFromResource(machineryId, ownerProfileId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(fleetManagementCommandService.handle(command),
                MachineryResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{machineryId}")
    @PreAuthorize(OWNER_ROLES)
    @Operation(summary = "Delete machinery", description = "Deletes a machine that is not reserved or rented.")
    public ResponseEntity<?> deleteMachinery(@PathVariable Long machineryId, Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var result = fleetManagementCommandService.handle(new DeleteMachineryCommand(machineryId, ownerProfileId));
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                id -> new MessageResource("Machinery %d deleted".formatted(id)), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "View machinery catalog", description = "Lists machinery, optionally filtered by category.")
    public ResponseEntity<List<MachineryResource>> getMachineryCatalog(@RequestParam(required = false) Long categoryId) {
        var machinery = fleetManagementQueryService.handle(new ViewMachineryCatalogQuery(categoryId)).stream()
                .map(MachineryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(machinery);
    }

    @GetMapping("/mine")
    @PreAuthorize(OWNER_ROLES)
    @Operation(summary = "View my machinery", description = "Lists the machines of the signed-in owner.")
    public ResponseEntity<List<MachineryResource>> getMyMachinery(Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var machinery = fleetManagementQueryService.handle(new GetMachineryByOwnerProfileIdQuery(ownerProfileId)).stream()
                .map(MachineryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(machinery);
    }

    @GetMapping("/{machineryId}")
    @Operation(summary = "Get machinery by id")
    public ResponseEntity<?> getMachineryById(@PathVariable Long machineryId) {
        return fleetManagementQueryService.handle(new GetMachineryByIdQuery(machineryId))
                .<ResponseEntity<?>>map(machinery -> ResponseEntity.ok(MachineryResourceFromEntityAssembler.toResourceFromEntity(machinery)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                        ApplicationError.notFound("Machinery", String.valueOf(machineryId))));
    }
}
