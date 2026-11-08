package com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.fleet.application.commandservices.FleetManagementCommandService;
import com.andesheavytech.maquicontrol.platform.fleet.application.queryservices.FleetManagementQueryService;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.CreateCategoryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.DeleteCategoryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.UpdateCategoryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.queries.GetCategoryByIdQuery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.queries.ViewCategoriesQuery;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources.CategoryResource;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources.CreateCategoryResource;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources.UpdateCategoryResource;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.transform.CategoryResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.interfaces.rest.resources.MessageResource;
import com.andesheavytech.maquicontrol.platform.shared.interfaces.rest.transform.ProblemDetailFromApplicationErrorAssembler;
import com.andesheavytech.maquicontrol.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Machinery category endpoints. Administrators manage the catalog; everyone signed in can read it.
 */
@RestController
@RequestMapping(value = "/api/v1/categories", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Categories", description = "Machinery category endpoints")
public class CategoriesController {

    private static final String ADMIN_ROLES = "hasAnyRole('FLEET_ADMINISTRATOR','SYSTEM_ADMINISTRATOR')";

    private final FleetManagementCommandService fleetManagementCommandService;
    private final FleetManagementQueryService fleetManagementQueryService;

    public CategoriesController(FleetManagementCommandService fleetManagementCommandService,
                                FleetManagementQueryService fleetManagementQueryService) {
        this.fleetManagementCommandService = fleetManagementCommandService;
        this.fleetManagementQueryService = fleetManagementQueryService;
    }

    @PostMapping
    @PreAuthorize(ADMIN_ROLES)
    @Operation(summary = "Create category")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CreateCategoryResource resource) {
        return ResponseEntityAssembler.toResponseEntityFromResult(
                fleetManagementCommandService.handle(new CreateCategoryCommand(resource.name())),
                CategoryResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize(ADMIN_ROLES)
    @Operation(summary = "Update category")
    public ResponseEntity<?> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody UpdateCategoryResource resource) {
        return ResponseEntityAssembler.toResponseEntityFromResult(
                fleetManagementCommandService.handle(new UpdateCategoryCommand(categoryId, resource.name())),
                CategoryResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize(ADMIN_ROLES)
    @Operation(summary = "Delete category", description = "Deletes a category that no machine uses.")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        return ResponseEntityAssembler.toResponseEntityFromResult(
                fleetManagementCommandService.handle(new DeleteCategoryCommand(categoryId)),
                id -> new MessageResource("Category %d deleted".formatted(id)), HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "View categories")
    public ResponseEntity<List<CategoryResource>> getAllCategories() {
        var categories = fleetManagementQueryService.handle(new ViewCategoriesQuery()).stream()
                .map(CategoryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get category by id")
    public ResponseEntity<?> getCategoryById(@PathVariable Long categoryId) {
        return fleetManagementQueryService.handle(new GetCategoryByIdQuery(categoryId))
                .<ResponseEntity<?>>map(category -> ResponseEntity.ok(CategoryResourceFromEntityAssembler.toResourceFromEntity(category)))
                .orElseGet(() -> ProblemDetailFromApplicationErrorAssembler.toResponseFromApplicationError(
                        ApplicationError.notFound("Category", String.valueOf(categoryId))));
    }
}
