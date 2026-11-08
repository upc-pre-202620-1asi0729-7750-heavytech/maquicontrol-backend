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
}
