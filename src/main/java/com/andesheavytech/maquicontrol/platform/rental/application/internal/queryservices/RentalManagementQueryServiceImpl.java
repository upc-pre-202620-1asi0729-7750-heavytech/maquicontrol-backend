package com.andesheavytech.maquicontrol.platform.rental.application.internal.queryservices;

import com.andesheavytech.maquicontrol.platform.rental.application.internal.outboundservices.acl.ExternalFleetService;
import com.andesheavytech.maquicontrol.platform.rental.application.queryservices.RentalManagementQueryService;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates.Rental;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.CheckMachineryAvailabilityQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalByIdQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalsDueToCompleteQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalsDueToStartQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.ViewFleetRentalsQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.ViewMyReservationsQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.ViewPendingRequestsQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalPeriod;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalStatus;
import com.andesheavytech.maquicontrol.platform.rental.domain.repositories.RentalRepository;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link RentalManagementQueryService}.
 */
@Service
public class RentalManagementQueryServiceImpl implements RentalManagementQueryService {

    private final RentalRepository rentalRepository;
    private final ExternalFleetService externalFleetService;

    public RentalManagementQueryServiceImpl(RentalRepository rentalRepository, ExternalFleetService externalFleetService) {
        this.rentalRepository = rentalRepository;
        this.externalFleetService = externalFleetService;
    }

    @Override
    public Optional<Rental> handle(GetRentalByIdQuery query) {
        return rentalRepository.findById(query.rentalId());
    }

    @Override
    public List<Rental> handle(ViewMyReservationsQuery query) {
        return rentalRepository.findByContractorProfileId(query.contractorProfileId());
    }

    @Override
    public List<Rental> handle(ViewPendingRequestsQuery query) {
        var machineryIds = externalFleetService.fetchMachineryIdsByOwnerProfileId(query.ownerProfileId());
        if (machineryIds.isEmpty()) {
            return List.of();
        }
        return rentalRepository.findByMachineryIdsAndStatus(machineryIds, RentalStatus.REQUESTED);
    }

    @Override
    public List<Rental> handle(ViewFleetRentalsQuery query) {
        var machineryIds = externalFleetService.fetchMachineryIdsByOwnerProfileId(query.ownerProfileId());
        if (machineryIds.isEmpty()) {
            return List.of();
        }
        return rentalRepository.findByMachineryIds(machineryIds);
    }

    @Override
    public Result<Boolean, ApplicationError> handle(CheckMachineryAvailabilityQuery query) {
        if (!externalFleetService.existsMachinery(query.machineryId())) {
            return Result.failure(ApplicationError.notFound("Machinery", String.valueOf(query.machineryId())));
        }
        try {
            var period = new RentalPeriod(query.startDate(), query.endDate());
            var available = externalFleetService.isMachineryRentable(query.machineryId())
                    && !rentalRepository.existsOverlapping(query.machineryId(),
                    EnumSet.of(RentalStatus.REQUESTED, RentalStatus.CONFIRMED, RentalStatus.IN_PROGRESS), period, null);
            return Result.success(available);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("period", e.getMessage()));
        }
    }

    @Override
    public List<Rental> handle(GetRentalsDueToStartQuery query) {
        return rentalRepository.findConfirmedStartingOnOrBefore(query.today());
    }

    @Override
    public List<Rental> handle(GetRentalsDueToCompleteQuery query) {
        return rentalRepository.findInProgressEndingBefore(query.today());
    }
}
