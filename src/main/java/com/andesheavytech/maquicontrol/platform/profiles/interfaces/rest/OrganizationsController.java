package com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.iam.interfaces.acl.IamContextFacade;
import com.andesheavytech.maquicontrol.platform.profiles.application.commandservices.ProfilesManagementCommandService;
import com.andesheavytech.maquicontrol.platform.profiles.application.queryservices.ProfilesManagementQueryService;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.aggregates.Profile;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.queries.GetOrganizationByIdQuery;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources.CreateOrganizationResource;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources.OrganizationResource;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources.UpdateOrganizationResource;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.transform.CreateOrganizationCommandFromResourceAssembler;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.transform.OrganizationResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.transform.UpdateOrganizationCommandFromResourceAssembler;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Organization endpoints. An organization is registered for the signed-in account's profile.
 */
@RestController
@RequestMapping(value = "/api/v1/organizations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Organizations", description = "Organization management endpoints")
public class OrganizationsController {

    private final ProfilesManagementCommandService profilesManagementCommandService;
    private final ProfilesManagementQueryService profilesManagementQueryService;
    private final IamContextFacade iamContextFacade;

    public OrganizationsController(ProfilesManagementCommandService profilesManagementCommandService,
                                   ProfilesManagementQueryService profilesManagementQueryService,
                                   IamContextFacade iamContextFacade) {
        this.profilesManagementCommandService = profilesManagementCommandService;
        this.profilesManagementQueryService = profilesManagementQueryService;
        this.iamContextFacade = iamContextFacade;
    }

    @PostMapping
    @Operation(summary = "Create organization", description = "Registers the organization of the signed-in account's profile.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Organization created",
                    content = @Content(schema = @Schema(implementation = OrganizationResource.class))),
            @ApiResponse(responseCode = "409", description = "Tax id already registered or profile already has one"),
            @ApiResponse(responseCode = "422", description = "The account has no profile yet")
    })
    public ResponseEntity<?> createOrganization(@Valid @RequestBody CreateOrganizationResource resource,
                                                Authentication authentication) {
        var userAccountId = iamContextFacade.fetchUserAccountIdByEmail(authentication.getName());
        var command = CreateOrganizationCommandFromResourceAssembler.toCommandFromResource(userAccountId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(profilesManagementCommandService.handle(command),
                OrganizationsController::toOrganizationResource, HttpStatus.CREATED);
    }

    @PutMapping("/{organizationId}")
    @Operation(summary = "Update organization")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Organization updated",
                    content = @Content(schema = @Schema(implementation = OrganizationResource.class))),
            @ApiResponse(responseCode = "403", description = "Organization of another account"),
            @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    public ResponseEntity<?> updateOrganization(@PathVariable Long organizationId,
                                                @Valid @RequestBody UpdateOrganizationResource resource,
                                                Authentication authentication) {
        var userAccountId = iamContextFacade.fetchUserAccountIdByEmail(authentication.getName());
        var command = UpdateOrganizationCommandFromResourceAssembler.toCommandFromResource(organizationId, userAccountId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(profilesManagementCommandService.handle(command),
                OrganizationsController::toOrganizationResource, HttpStatus.OK);
    }

    @GetMapping("/{organizationId}")
    @Operation(summary = "Get organization by id")
    public ResponseEntity<?> getOrganizationById(@PathVariable Long organizationId) {
        return profilesManagementQueryService.handle(new GetOrganizationByIdQuery(organizationId))
                .<ResponseEntity<?>>map(organization -> ResponseEntity.ok(OrganizationResourceFromEntityAssembler.toResourceFromEntity(organization)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                        ApplicationError.notFound("Organization", String.valueOf(organizationId))));
    }

    private static OrganizationResource toOrganizationResource(Profile profile) {
        return OrganizationResourceFromEntityAssembler.toResourceFromEntity(profile.getOrganization());
    }
}
