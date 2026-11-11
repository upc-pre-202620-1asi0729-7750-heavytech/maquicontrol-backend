package com.andesheavytech.maquicontrol.platform.fleet.application.internal.queryservices;

import com.andesheavytech.maquicontrol.platform.fleet.application.queryservices.FleetManagementQueryService;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Category;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Machinery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.queries.GetCategoryByIdQuery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.queries.GetMachineryByIdQuery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.queries.GetMachineryByOwnerProfileIdQuery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.queries.ViewCategoriesQuery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.queries.ViewMachineryCatalogQuery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.repositories.CategoryRepository;
import com.andesheavytech.maquicontrol.platform.fleet.domain.repositories.MachineryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link FleetManagementQueryService}.
 */
@Service
public class FleetManagementQueryServiceImpl implements FleetManagementQueryService {

    private final MachineryRepository machineryRepository;
    private final CategoryRepository categoryRepository;

    public FleetManagementQueryServiceImpl(MachineryRepository machineryRepository, CategoryRepository categoryRepository) {
        this.machineryRepository = machineryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Machinery> handle(ViewMachineryCatalogQuery query) {
        return query.categoryId() == null
                ? machineryRepository.findAll()
                : machineryRepository.findByCategoryId(query.categoryId());
    }

    @Override
    public Optional<Machinery> handle(GetMachineryByIdQuery query) {
        return machineryRepository.findById(query.machineryId());
    }

    @Override
    public List<Machinery> handle(GetMachineryByOwnerProfileIdQuery query) {
        return machineryRepository.findByOwnerProfileId(query.ownerProfileId());
    }

    @Override
    public List<Category> handle(ViewCategoriesQuery query) {
        return categoryRepository.findAll();
    }

    @Override
    public Optional<Category> handle(GetCategoryByIdQuery query) {
        return categoryRepository.findById(query.categoryId());
    }
}
