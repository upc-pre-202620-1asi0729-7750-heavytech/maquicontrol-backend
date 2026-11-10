package com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.iam.interfaces.acl.IamContextFacade;
import com.andesheavytech.maquicontrol.platform.profiles.application.commandservices.ProfilesManagementCommandService;
import com.andesheavytech.maquicontrol.platform.profiles.application.queryservices.ProfilesManagementQueryService;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.queries.GetProfileByUserAccountIdQuery;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.queries.ViewProfileQuery;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources.CreateProfileResource;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources.ProfileResource;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources.UpdateProfileResource;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.transform.CreateProfileCommandFromResourceAssembler;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
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
 * Profile endpoints. The profile always belongs to the signed-in account.
 */
@RestController
@RequestMapping(value = "/api/v1/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "Profile management endpoints")
public class ProfilesController {

    private final ProfilesManagementCommandService profilesManagementCommandService;
    private final ProfilesManagementQueryService profilesManagementQueryService;
    private final IamContextFacade iamContextFacade;

    public ProfilesController(ProfilesManagementCommandService profilesManagementCommandService,
                              ProfilesManagementQueryService profilesManagementQueryService,
                              IamContextFacade iamContextFacade) {
        this.profilesManagementCommandService = profilesManagementCommandService;
        this.profilesManagementQueryService = profilesManagementQueryService;
        this.iamContextFacade = iamContextFacade;
    }

    @PostMapping
    @Operation(summary = "Create profile", description = "Creates the profile of the signed-in account.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Profile or document already registered")
    })
    public ResponseEntity<?> createProfile(@Valid @RequestBody CreateProfileResource resource, Authentication authentication) {
        var userAccountId = iamContextFacade.fetchUserAccountIdByEmail(authentication.getName());
        var command = CreateProfileCommandFromResourceAssembler.toCommandFromResource(userAccountId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(profilesManagementCommandService.handle(command),
                ProfileResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{profileId}")
    @Operation(summary = "Update profile", description = "Updates the profile of the signed-in account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "403", description = "Profile of another account"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<?> updateProfile(@PathVariable Long profileId, @Valid @RequestBody UpdateProfileResource resource,
                                           Authentication authentication) {
        var userAccountId = iamContextFacade.fetchUserAccountIdByEmail(authentication.getName());
        var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(profileId, userAccountId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(profilesManagementCommandService.handle(command),
                ProfileResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @GetMapping("/me")
    @Operation(summary = "Get my profile", description = "Returns the profile of the signed-in account.")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        var userAccountId = iamContextFacade.fetchUserAccountIdByEmail(authentication.getName());
        return profilesManagementQueryService.handle(new GetProfileByUserAccountIdQuery(userAccountId))
                .<ResponseEntity<?>>map(profile -> ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                        ApplicationError.notFound("Profile", "signed-in account")));
    }

    @GetMapping("/{profileId}")
    @Operation(summary = "Get profile by id")
    public ResponseEntity<?> getProfileById(@PathVariable Long profileId) {
        return profilesManagementQueryService.handle(new ViewProfileQuery(profileId))
                .<ResponseEntity<?>>map(profile -> ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                        ApplicationError.notFound("Profile", String.valueOf(profileId))));
    }
}
