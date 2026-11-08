package com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.adapters;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Category;
import com.andesheavytech.maquicontrol.platform.fleet.domain.repositories.CategoryRepository;
import com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.assemblers.CategoryPersistenceAssembler;
import com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.repositories.CategoryPersistenceRepository;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryPersistenceRepository categoryPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public CategoryRepositoryImpl(CategoryPersistenceRepository categoryPersistenceRepository,
                                  DomainEventPublisher domainEventPublisher) {
        this.categoryPersistenceRepository = categoryPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Category save(Category category) {
        var saved = categoryPersistenceRepository.save(CategoryPersistenceAssembler.toPersistenceFromDomain(category));
        domainEventPublisher.publishAndClearEvents(category);
        return CategoryPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryPersistenceRepository.findById(id).map(CategoryPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Category> findAll() {
        return categoryPersistenceRepository.findAll().stream()
                .map(CategoryPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return id != null && categoryPersistenceRepository.existsById(id);
    }

    @Override
    public boolean existsByCode(String code, Long excludedCategoryId) {
        return excludedCategoryId == null
                ? categoryPersistenceRepository.existsByCode(code)
                : categoryPersistenceRepository.existsByCodeAndIdNot(code, excludedCategoryId);
    }

    @Override
    public void deleteById(Long id) {
        categoryPersistenceRepository.deleteById(id);
    }
}
