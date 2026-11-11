package com.andesheavytech.maquicontrol.platform.operations.application.internal.outboundservices.acl;

import com.andesheavytech.maquicontrol.platform.rental.interfaces.acl.RentalContextFacade;
import org.springframework.stereotype.Service;

/**
 * Anti-corruption layer towards the Rental Management bounded context.
 */
@Service("operationsExternalRentalService")
public class ExternalRentalService {

    private final RentalContextFacade rentalContextFacade;

    public ExternalRentalService(RentalContextFacade rentalContextFacade) {
        this.rentalContextFacade = rentalContextFacade;
    }

    public boolean existsRental(Long rentalId) {
        return rentalContextFacade.existsRental(rentalId);
    }

    public String fetchRentalStatus(Long rentalId) {
        return rentalContextFacade.fetchRentalStatus(rentalId);
    }

    public Long fetchRentalMachineryId(Long rentalId) {
        return rentalContextFacade.fetchRentalMachineryId(rentalId);
    }

    public Long fetchRentalContractorProfileId(Long rentalId) {
        return rentalContextFacade.fetchRentalContractorProfileId(rentalId);
    }
}
