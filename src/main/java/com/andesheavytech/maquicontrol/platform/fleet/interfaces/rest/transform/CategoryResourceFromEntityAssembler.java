package com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Category;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources.CategoryResource;

public final class CategoryResourceFromEntityAssembler {

    private CategoryResourceFromEntityAssembler() {
    }

    public static CategoryResource toResourceFromEntity(Category category) {
        return new CategoryResource(category.getId(), category.getCode(), category.getName());
    }
}
