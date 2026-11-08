package com.andesheavytech.maquicontrol.platform.fleet.domain.repositories;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(Long id);

    List<Category> findAll();

    boolean existsById(Long id);

    boolean existsByCode(String code, Long excludedCategoryId);

    void deleteById(Long id);
}
