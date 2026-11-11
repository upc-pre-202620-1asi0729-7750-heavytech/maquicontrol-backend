package com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.assemblers;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Category;
import com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.entities.CategoryPersistenceEntity;

public final class CategoryPersistenceAssembler {

    private CategoryPersistenceAssembler() {
    }

    public static Category toDomainFromPersistence(CategoryPersistenceEntity entity) {
        return Category.reconstitute(entity.getId(), entity.getCode(), entity.getName());
    }

    public static CategoryPersistenceEntity toPersistenceFromDomain(Category category) {
        var entity = new CategoryPersistenceEntity();
        if (category.getId() != null) {
            entity.setId(category.getId());
        }
        entity.setCode(category.getCode());
        entity.setName(category.getName());
        return entity;
    }
}
