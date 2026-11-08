package com.andesheavytech.maquicontrol.platform.iam.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.iam.application.queryservices.IdentityAccessQueryService;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.queries.ViewRolesQuery;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.resources.RoleResource;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.transform.RoleResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Role catalog, shown on the sign up form.
 */
@RestController
@RequestMapping(value = "/api/v1/roles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Roles", description = "Role catalog")
@SecurityRequirements
public class RolesController {

    private final IdentityAccessQueryService identityAccessQueryService;

    public RolesController(IdentityAccessQueryService identityAccessQueryService) {
        this.identityAccessQueryService = identityAccessQueryService;
    }

    @GetMapping
    @Operation(summary = "View roles", description = "Lists every role of the platform.")
    public ResponseEntity<List<RoleResource>> getAllRoles() {
        var roles = identityAccessQueryService.handle(new ViewRolesQuery()).stream()
                .map(RoleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(roles);
    }
}
