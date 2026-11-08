package com.andesheavytech.maquicontrol.platform.rental.application.acl;

import com.andesheavytech.maquicontrol.platform.rental.application.queryservices.RentalManagementQueryService;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalsByContractorProfileIdQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalStatus;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.acl.RentalContextFacade;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link RentalContextFacade}.
 */
@Service
public class RentalContextFacadeImpl implements RentalContextFacade {

    private final RentalManagementQueryService rentalManagementQueryService;

    public RentalContextFacadeImpl(RentalManagementQueryService rentalManagementQueryService) {
        this.rentalManagementQueryService = rentalManagementQueryService;
    }

    @Override
    public boolean existsRentalInProgress(Long machineryId, Long contractorProfileId) {
        if (machineryId == null || contractorProfileId == null || contractorProfileId <= 0) {
            return false;
        }
        return rentalManagementQueryService.handle(new GetRentalsByContractorProfileIdQuery(contractorProfileId)).stream()
                .anyMatch(rental -> rental.getMachineryId().equals(machineryId)
                        && rental.getStatus() == RentalStatus.IN_PROGRESS);
    }

    @Override
    public boolean existsRental(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return false;
        }
        return rentalManagementQueryService.handle(new com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalByIdQuery(rentalId)).isPresent();
    }

    @Override
    public Long fetchRentalMachineryId(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return 0L;
        }
        return rentalManagementQueryService.handle(new com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalByIdQuery(rentalId))
                .map(com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates.Rental::getMachineryId)
                .orElse(0L);
    }

    @Override
    public Long fetchRentalContractorProfileId(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return 0L;
        }
        return rentalManagementQueryService.handle(new com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalByIdQuery(rentalId))
                .map(com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates.Rental::getContractorProfileId)
                .orElse(0L);
    }

    @Override
    public String fetchRentalStatus(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return "";
        }
        return rentalManagementQueryService.handle(new com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalByIdQuery(rentalId))
                .map(rental -> rental.getStatus().name())
                .orElse("");
    }

    @Override
    public java.math.BigDecimal fetchRentalTotalAmount(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return null;
        }
        return rentalManagementQueryService.handle(new com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalByIdQuery(rentalId))
                .map(rental -> rental.getTotalAmount().amount())
                .orElse(null);
    }

    @Override
    public String fetchRentalCurrency(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return "";
        }
        return rentalManagementQueryService.handle(new com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalByIdQuery(rentalId))
                .map(rental -> rental.getTotalAmount().currency().name())
                .orElse("");
    }

    @Override
    public java.time.LocalDate fetchRentalEndDate(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return null;
        }
        return rentalManagementQueryService.handle(new com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalByIdQuery(rentalId))
                .map(rental -> rental.getPeriod().endDate())
                .orElse(null);
    }
}
