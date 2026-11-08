package com.andesheavytech.maquicontrol.platform.fleet.domain.model.queries;

/**
 * Lists published machinery, optionally filtered by category.
 *
 * @param categoryId the category, or {@code null} for every category
 */
public record ViewMachineryCatalogQuery(Long categoryId) {
}
