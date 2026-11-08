package com.andesheavytech.maquicontrol.platform.iam.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.iam.application.commandservices.IdentityAccessCommandService;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.resources.AuthenticatedUserAccountResource;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.resources.SignInResource;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.resources.SignUpResource;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.resources.UserAccountResource;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.transform.AuthenticatedUserAccountResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.transform.UserAccountResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sign up and sign in endpoints.
 */
@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Sign up and sign in endpoints")
@SecurityRequirements
public class AuthenticationController {

    private final IdentityAccessCommandService identityAccessCommandService;

    public AuthenticationController(IdentityAccessCommandService identityAccessCommandService) {
        this.identityAccessCommandService = identityAccessCommandService;
    }

    @PostMapping("/sign-up")
    @Operation(summary = "Sign up", description = "Creates a user account with the chosen role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created",
                    content = @Content(schema = @Schema(implementation = UserAccountResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data or role"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpResource resource) {
        var result = identityAccessCommandService.handle(SignUpCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                UserAccountResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Sign in", description = "Checks the credentials and returns a bearer token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Signed in",
                    content = @Content(schema = @Schema(implementation = AuthenticatedUserAccountResource.class))),
            @ApiResponse(responseCode = "401", description = "Wrong email or password"),
            @ApiResponse(responseCode = "403", description = "Account not active")
    })
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInResource resource) {
        var result = identityAccessCommandService.handle(SignInCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityAssembler.toResponseEntityFromResult(result,
                AuthenticatedUserAccountResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
