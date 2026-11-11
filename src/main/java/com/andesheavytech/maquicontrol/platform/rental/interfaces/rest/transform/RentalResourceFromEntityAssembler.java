package com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates.Rental;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.resources.RentalAmountResource;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.resources.RentalPeriodResource;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.resources.RentalResource;

public final class RentalResourceFromEntityAssembler {

    private RentalResourceFromEntityAssembler() {
    }

    public static RentalResource toResourceFromEntity(Rental rental) {
        return new RentalResource(rental.getId(), rental.getMachineryId(), rental.getContractorProfileId(),
                new RentalPeriodResource(rental.getPeriod().startDate(), rental.getPeriod().endDate()),
                rental.getPeriod().durationInDays(), rental.getStatus().name(),
                new RentalAmountResource(rental.getTotalAmount().amount(), rental.getTotalAmount().currency().name()),
                rental.getCancellationReason(), rental.getRequestedAt(), rental.getConfirmedAt(), rental.getCancelledAt());
    }
}
