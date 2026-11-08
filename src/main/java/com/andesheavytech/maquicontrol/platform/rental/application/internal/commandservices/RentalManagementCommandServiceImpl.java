package com.andesheavytech.maquicontrol.platform.rental.application.internal.commandservices;

import com.andesheavytech.maquicontrol.platform.rental.application.commandservices.RentalManagementCommandService;
import com.andesheavytech.maquicontrol.platform.rental.application.internal.outboundservices.acl.ExternalFleetService;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates.Rental;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.CancelRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.CompleteRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.ConfirmRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.RejectRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.RequestRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.StartRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalPeriod;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalStatus;
import com.andesheavytech.maquicontrol.platform.rental.domain.repositories.RentalRepository;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.function.Consumer;

/**
 * Implementation of {@link RentalManagementCommandService}.
 */
@Service
public class RentalManagementCommandServiceImpl implements RentalManagementCommandService {

    private static final EnumSet<RentalStatus> BLOCKING_STATUSES =
            EnumSet.of(RentalStatus.REQUESTED, RentalStatus.CONFIRMED, RentalStatus.IN_PROGRESS);
    private static final EnumSet<RentalStatus> COMMITTED_STATUSES =
            EnumSet.of(RentalStatus.CONFIRMED, RentalStatus.IN_PROGRESS);

    private final RentalRepository rentalRepository;
    private final ExternalFleetService externalFleetService;

    public RentalManagementCommandServiceImpl(RentalRepository rentalRepository, ExternalFleetService externalFleetService) {
        this.rentalRepository = rentalRepository;
        this.externalFleetService = externalFleetService;
    }

    @Override
    @Transactional
    public Result<Rental, ApplicationError> handle(RequestRentalCommand command) {
        if (command.contractorProfileId() == null || command.contractorProfileId() <= 0) {
            return Result.failure(ApplicationError.businessRuleViolation("profile required",
                    "create your profile before requesting a rental"));
        }
        if (!externalFleetService.existsMachinery(command.machineryId())) {
            return Result.failure(ApplicationError.notFound("Machinery", String.valueOf(command.machineryId())));
        }
        if (!externalFleetService.isMachineryRentable(command.machineryId())) {
            return Result.failure(ApplicationError.businessRuleViolation("machinery not rentable",
                    "the machinery is in maintenance or out of service"));
        }
        if (externalFleetService.fetchOwnerProfileId(command.machineryId())
                .filter(ownerProfileId -> ownerProfileId.equals(command.contractorProfileId())).isPresent()) {
            return Result.failure(ApplicationError.businessRuleViolation("own machinery",
                    "you cannot rent your own machinery"));
        }
        var hourlyRate = externalFleetService.fetchHourlyRate(command.machineryId());
        if (hourlyRate.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Machinery", String.valueOf(command.machineryId())));
        }
        try {
            var period = new RentalPeriod(command.startDate(), command.endDate());
            if (rentalRepository.existsOverlapping(command.machineryId(), BLOCKING_STATUSES, period, null)) {
                return Result.failure(ApplicationError.conflict("rental",
                        "the machinery already has a rental for those dates"));
            }
            var rental = Rental.request(command.machineryId(), command.contractorProfileId(), period,
                    hourlyRate.get(), LocalDate.now());
            return Result.success(rentalRepository.save(rental));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("rental", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Rental, ApplicationError> handle(ConfirmRentalCommand command) {
        var found = findRentalOfOwner(command.rentalId(), command.ownerProfileId());
        if (found.isFailure()) {
            return found;
        }
        var rental = found.getOrElse(null);
        if (rentalRepository.existsOverlapping(rental.getMachineryId(), COMMITTED_STATUSES, rental.getPeriod(), rental.getId())) {
            return Result.failure(ApplicationError.conflict("rental",
                    "another confirmed rental already takes those dates"));
        }
        return applyTransition(rental, Rental::confirm);
    }

    @Override
    @Transactional
    public Result<Rental, ApplicationError> handle(RejectRentalCommand command) {
        var found = findRentalOfOwner(command.rentalId(), command.ownerProfileId());
        if (found.isFailure()) {
            return found;
        }
        return applyTransition(found.getOrElse(null), rental -> rental.reject(command.reason()));
    }

    @Override
    @Transactional
    public Result<Rental, ApplicationError> handle(CancelRentalCommand command) {
        var found = rentalRepository.findById(command.rentalId());
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Rental", String.valueOf(command.rentalId())));
        }
        var rental = found.get();
        var isOwner = externalFleetService.fetchOwnerProfileId(rental.getMachineryId())
                .filter(ownerProfileId -> ownerProfileId.equals(command.requesterProfileId())).isPresent();
        if (!rental.isRequestedBy(command.requesterProfileId()) && !isOwner) {
            return Result.failure(ApplicationError.forbidden("rental", "only the contractor or the owner can cancel this rental"));
        }
        return applyTransition(rental, current -> current.cancel(command.reason()));
    }

    @Override
    @Transactional
    public Result<Rental, ApplicationError> handle(StartRentalCommand command) {
        return rentalRepository.findById(command.rentalId())
                .map(rental -> applyTransition(rental, Rental::start))
                .orElseGet(() -> Result.failure(ApplicationError.notFound("Rental", String.valueOf(command.rentalId()))));
    }

    @Override
    @Transactional
    public Result<Rental, ApplicationError> handle(CompleteRentalCommand command) {
        return rentalRepository.findById(command.rentalId())
                .map(rental -> applyTransition(rental, Rental::complete))
                .orElseGet(() -> Result.failure(ApplicationError.notFound("Rental", String.valueOf(command.rentalId()))));
    }

    private Result<Rental, ApplicationError> findRentalOfOwner(Long rentalId, Long ownerProfileId) {
        var found = rentalRepository.findById(rentalId);
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Rental", String.valueOf(rentalId)));
        }
        var isOwner = externalFleetService.fetchOwnerProfileId(found.get().getMachineryId())
                .filter(id -> id.equals(ownerProfileId)).isPresent();
        if (!isOwner) {
            return Result.failure(ApplicationError.forbidden("rental", "only the machinery owner can answer this request"));
        }
        return Result.success(found.get());
    }

    private Result<Rental, ApplicationError> applyTransition(Rental rental, Consumer<Rental> transition) {
        try {
            transition.accept(rental);
            return Result.success(rentalRepository.save(rental));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("rental status", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("rental", e.getMessage()));
        }
    }
}
