package com.andesheavytech.maquicontrol.platform.rental.application.acl;

import com.andesheavytech.maquicontrol.platform.rental.application.internal.outboundservices.acl.ExternalFleetService;
import com.andesheavytech.maquicontrol.platform.rental.application.queryservices.RentalManagementQueryService;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates.Rental;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalByIdQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.ViewMyReservationsQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalStatus;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.acl.RentalContextFacade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Implementation of {@link RentalContextFacade}.
 */
@Service
public class RentalContextFacadeImpl implements RentalContextFacade {

    private final RentalManagementQueryService rentalManagementQueryService;
    private final ExternalFleetService externalFleetService;

    public RentalContextFacadeImpl(RentalManagementQueryService rentalManagementQueryService,
                                   ExternalFleetService externalFleetService) {
        this.rentalManagementQueryService = rentalManagementQueryService;
        this.externalFleetService = externalFleetService;
    }

    @Override
    public boolean existsRentalInProgress(Long machineryId, Long contractorProfileId) {
        if (machineryId == null || contractorProfileId == null || contractorProfileId <= 0) {
            return false;
        }
        return rentalManagementQueryService.handle(new ViewMyReservationsQuery(contractorProfileId)).stream()
                .anyMatch(rental -> rental.getMachineryId().equals(machineryId)
                        && rental.getStatus() == RentalStatus.IN_PROGRESS);
    }

    @Override
    public boolean existsRental(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return false;
        }
        return rentalManagementQueryService.handle(new GetRentalByIdQuery(rentalId)).isPresent();
    }

    @Override
    public Long fetchRentalMachineryId(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return 0L;
        }
        return rentalManagementQueryService.handle(new GetRentalByIdQuery(rentalId))
                .map(Rental::getMachineryId)
                .orElse(0L);
    }

    @Override
    public Long fetchRentalContractorProfileId(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return 0L;
        }
        return rentalManagementQueryService.handle(new GetRentalByIdQuery(rentalId))
                .map(Rental::getContractorProfileId)
                .orElse(0L);
    }

    @Override
    public String fetchRentalStatus(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return "";
        }
        return rentalManagementQueryService.handle(new GetRentalByIdQuery(rentalId))
                .map(rental -> rental.getStatus().name())
                .orElse("");
    }

    @Override
    public BigDecimal fetchRentalTotalAmount(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return null;
        }
        return rentalManagementQueryService.handle(new GetRentalByIdQuery(rentalId))
                .map(rental -> rental.getTotalAmount().amount())
                .orElse(null);
    }

    @Override
    public String fetchRentalCurrency(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return "";
        }
        return rentalManagementQueryService.handle(new GetRentalByIdQuery(rentalId))
                .map(rental -> rental.getTotalAmount().currency().name())
                .orElse("");
    }

    @Override
    public LocalDate fetchRentalEndDate(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return null;
        }
        return rentalManagementQueryService.handle(new GetRentalByIdQuery(rentalId))
                .map(rental -> rental.getPeriod().endDate())
                .orElse(null);
    }

    @Override
    public boolean isRentalParticipant(Long rentalId, Long profileId) {
        if (rentalId == null || rentalId <= 0 || profileId == null || profileId <= 0) {
            return false;
        }
        return rentalManagementQueryService.handle(new GetRentalByIdQuery(rentalId))
                .map(rental -> profileId.equals(rental.getContractorProfileId())
                        || externalFleetService.fetchOwnerProfileId(rental.getMachineryId()).map(profileId::equals).orElse(false))
                .orElse(false);
    }
}
