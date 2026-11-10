package com.andesheavytech.maquicontrol.platform.billing.application.internal.outboundservices.acl;

import com.andesheavytech.maquicontrol.platform.rental.interfaces.acl.RentalContextFacade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Anti-corruption layer towards the Rental Management bounded context.
 */
@Service("billingExternalRentalService")
public class ExternalRentalService {

    private final RentalContextFacade rentalContextFacade;

    public ExternalRentalService(RentalContextFacade rentalContextFacade) {
        this.rentalContextFacade = rentalContextFacade;
    }

    public boolean existsRental(Long rentalId) {
        return rentalContextFacade.existsRental(rentalId);
    }

    public Long fetchRentalMachineryId(Long rentalId) {
        return rentalContextFacade.fetchRentalMachineryId(rentalId);
    }

    public Long fetchRentalContractorProfileId(Long rentalId) {
        return rentalContextFacade.fetchRentalContractorProfileId(rentalId);
    }

    public String fetchRentalStatus(Long rentalId) {
        return rentalContextFacade.fetchRentalStatus(rentalId);
    }

    public BigDecimal fetchRentalTotalAmount(Long rentalId) {
        return rentalContextFacade.fetchRentalTotalAmount(rentalId);
    }

    public String fetchRentalCurrency(Long rentalId) {
        return rentalContextFacade.fetchRentalCurrency(rentalId);
    }

    public LocalDate fetchRentalEndDate(Long rentalId) {
        return rentalContextFacade.fetchRentalEndDate(rentalId);
    }
}
