package com.andesheavytech.maquicontrol.platform.maintenance.application.internal.outboundservices.acl;

import com.andesheavytech.maquicontrol.platform.rental.interfaces.acl.RentalContextFacade;
import org.springframework.stereotype.Service;

/**
 * Anti-corruption layer towards the Rental Management bounded context.
 */
@Service
public class ExternalRentalService {

    private final RentalContextFacade rentalContextFacade;

    public ExternalRentalService(RentalContextFacade rentalContextFacade) {
        this.rentalContextFacade = rentalContextFacade;
    }

    public boolean isRentingMachinery(Long contractorProfileId, Long machineryId) {
        return rentalContextFacade.existsRentalInProgress(machineryId, contractorProfileId);
    }
}
