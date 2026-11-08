package com.andesheavytech.maquicontrol.platform.fleet.application.internal.commandservices;

import com.andesheavytech.maquicontrol.platform.fleet.application.commandservices.FleetManagementCommandService;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Category;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Machinery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.ChangeMachineryStatusCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.CreateCategoryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.DeleteCategoryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.DeleteMachineryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.FinishMachineryMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.MarkMachineryAsRentedCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.PublishMachineryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.ReleaseMachineryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.ReserveMachineryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.SeedCategoriesCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.StartMachineryMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.UpdateCategoryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.UpdateMachineryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects.MachineryLocation;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects.MachinerySpecification;
import com.andesheavytech.maquicontrol.platform.fleet.domain.repositories.CategoryRepository;
import com.andesheavytech.maquicontrol.platform.fleet.domain.repositories.MachineryRepository;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

/**
 * Implementation of {@link FleetManagementCommandService}.
 */
@Service
public class FleetManagementCommandServiceImpl implements FleetManagementCommandService {

    private static final List<String> DEFAULT_CATEGORIES = List.of(
            "Excavadoras", "Retroexcavadoras", "Cargadores frontales", "Tractores de oruga",
            "Motoniveladoras", "Rodillos compactadores", "Volquetes", "Grúas");

    private final MachineryRepository machineryRepository;
    private final CategoryRepository categoryRepository;

    public FleetManagementCommandServiceImpl(MachineryRepository machineryRepository,
                                             CategoryRepository categoryRepository) {
        this.machineryRepository = machineryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Result<Machinery, ApplicationError> handle(PublishMachineryCommand command) {
        if (command.ownerProfileId() == null || command.ownerProfileId() <= 0) {
            return Result.failure(ApplicationError.businessRuleViolation("profile required",
                    "create your profile before publishing machinery"));
        }
        if (command.categoryId() == null || !categoryRepository.existsById(command.categoryId())) {
            return Result.failure(ApplicationError.notFound("Category", String.valueOf(command.categoryId())));
        }
        try {
            var machinery = Machinery.publish(command.ownerProfileId(), command.categoryId(), command.name(),
                    command.description(),
                    new MachinerySpecification(command.brand(), command.model(), command.manufactureYear()),
                    new Money(command.hourlyRateAmount(), command.hourlyRateCurrency()),
                    new MachineryLocation(command.department(), command.province(), command.district(),
                            command.address(), command.latitude(), command.longitude()));
            return Result.success(machineryRepository.save(machinery));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("machinery", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Machinery, ApplicationError> handle(UpdateMachineryCommand command) {
        var found = findOwnedMachinery(command.machineryId(), command.ownerProfileId());
        if (found.isFailure()) {
            return found;
        }
        if (command.categoryId() == null || !categoryRepository.existsById(command.categoryId())) {
            return Result.failure(ApplicationError.notFound("Category", String.valueOf(command.categoryId())));
        }
        var machinery = found.getOrElse(null);
        try {
            machinery.update(command.categoryId(), command.name(), command.description(),
                    new MachinerySpecification(command.brand(), command.model(), command.manufactureYear()),
                    new Money(command.hourlyRateAmount(), command.hourlyRateCurrency()),
                    new MachineryLocation(command.department(), command.province(), command.district(),
                            command.address(), command.latitude(), command.longitude()));
            return Result.success(machineryRepository.save(machinery));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("machinery", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Machinery, ApplicationError> handle(ChangeMachineryStatusCommand command) {
        var found = findOwnedMachinery(command.machineryId(), command.ownerProfileId());
        if (found.isFailure()) {
            return found;
        }
        var machinery = found.getOrElse(null);
        try {
            machinery.changeStatus(command.status());
            return Result.success(machineryRepository.save(machinery));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("status", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("machinery status", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Long, ApplicationError> handle(DeleteMachineryCommand command) {
        var found = findOwnedMachinery(command.machineryId(), command.ownerProfileId());
        if (found.isFailure()) {
            return found.map(Machinery::getId);
        }
        var machinery = found.getOrElse(null);
        if (!machinery.canBeDeleted()) {
            return Result.failure(ApplicationError.businessRuleViolation("machinery in use",
                    "machinery is %s and cannot be deleted".formatted(machinery.getStatus())));
        }
        machineryRepository.deleteById(machinery.getId());
        return Result.success(machinery.getId());
    }

    @Override
    @Transactional
    public Result<Machinery, ApplicationError> handle(ReserveMachineryCommand command) {
        return applyStatusTransition(command.machineryId(), Machinery::reserve);
    }

    @Override
    @Transactional
    public Result<Machinery, ApplicationError> handle(MarkMachineryAsRentedCommand command) {
        return applyStatusTransition(command.machineryId(), Machinery::markAsRented);
    }

    @Override
    @Transactional
    public Result<Machinery, ApplicationError> handle(ReleaseMachineryCommand command) {
        return applyStatusTransition(command.machineryId(), Machinery::release);
    }

    @Override
    @Transactional
    public Result<Machinery, ApplicationError> handle(StartMachineryMaintenanceCommand command) {
        var found = machineryRepository.findById(command.machineryId());
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Machinery", String.valueOf(command.machineryId())));
        }
        var machinery = found.get();
        try {
            machinery.startMaintenance();
            return Result.success(machineryRepository.save(machinery));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("machinery status", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Machinery, ApplicationError> handle(FinishMachineryMaintenanceCommand command) {
        return applyStatusTransition(command.machineryId(), Machinery::finishMaintenance);
    }

    @Override
    @Transactional
    public Result<Category, ApplicationError> handle(CreateCategoryCommand command) {
        try {
            var category = Category.create(command.name());
            if (categoryRepository.existsByCode(category.getCode(), null)) {
                return Result.failure(ApplicationError.conflict("category", "category %s already exists".formatted(category.getName())));
            }
            return Result.success(categoryRepository.save(category));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("category", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Category, ApplicationError> handle(UpdateCategoryCommand command) {
        var found = categoryRepository.findById(command.categoryId());
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Category", String.valueOf(command.categoryId())));
        }
        var category = found.get();
        try {
            category.rename(command.name());
            if (categoryRepository.existsByCode(category.getCode(), category.getId())) {
                return Result.failure(ApplicationError.conflict("category", "category %s already exists".formatted(category.getName())));
            }
            return Result.success(categoryRepository.save(category));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("category", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Long, ApplicationError> handle(DeleteCategoryCommand command) {
        if (!categoryRepository.existsById(command.categoryId())) {
            return Result.failure(ApplicationError.notFound("Category", String.valueOf(command.categoryId())));
        }
        if (machineryRepository.existsByCategoryId(command.categoryId())) {
            return Result.failure(ApplicationError.businessRuleViolation("category in use",
                    "the category has machinery and cannot be deleted"));
        }
        categoryRepository.deleteById(command.categoryId());
        return Result.success(command.categoryId());
    }

    @Override
    @Transactional
    public void handle(SeedCategoriesCommand command) {
        DEFAULT_CATEGORIES.stream()
                .map(Category::create)
                .filter(category -> !categoryRepository.existsByCode(category.getCode(), null))
                .forEach(categoryRepository::save);
    }

    private Result<Machinery, ApplicationError> findOwnedMachinery(Long machineryId, Long ownerProfileId) {
        var found = machineryRepository.findById(machineryId);
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Machinery", String.valueOf(machineryId)));
        }
        if (!found.get().isOwnedBy(ownerProfileId)) {
            return Result.failure(ApplicationError.forbidden("machinery", "only the owner can change this machinery"));
        }
        return Result.success(found.get());
    }

    private Result<Machinery, ApplicationError> applyStatusTransition(Long machineryId, Consumer<Machinery> transition) {
        var found = machineryRepository.findById(machineryId);
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Machinery", String.valueOf(machineryId)));
        }
        var machinery = found.get();
        transition.accept(machinery);
        return Result.success(machineryRepository.save(machinery));
    }
}
