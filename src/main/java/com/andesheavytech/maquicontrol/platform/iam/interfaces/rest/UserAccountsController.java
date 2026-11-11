package com.andesheavytech.maquicontrol.platform.iam.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.iam.application.queryservices.IdentityAccessQueryService;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.queries.GetSignedInAccountQuery;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.resources.UserAccountResource;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.transform.UserAccountResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints about the signed-in account.
 */
@RestController
@RequestMapping(value = "/api/v1/user-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User Accounts", description = "Signed-in user account")
public class UserAccountsController {

    private final IdentityAccessQueryService identityAccessQueryService;

    public UserAccountsController(IdentityAccessQueryService identityAccessQueryService) {
        this.identityAccessQueryService = identityAccessQueryService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get signed-in account", description = "Returns the account of the bearer token.")
    @ApiResponse(responseCode = "200", description = "Account found",
            content = @Content(schema = @Schema(implementation = UserAccountResource.class)))
    public ResponseEntity<?> getSignedInAccount(Authentication authentication) {
        return identityAccessQueryService.handle(new GetSignedInAccountQuery(authentication.getName()))
                .<ResponseEntity<?>>map(account -> ResponseEntity.ok(UserAccountResourceFromEntityAssembler.toResourceFromEntity(account)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                        ApplicationError.notFound("UserAccount", authentication.getName())));
    }
}
