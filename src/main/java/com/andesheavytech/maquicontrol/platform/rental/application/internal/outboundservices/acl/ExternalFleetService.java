package com.andesheavytech.maquicontrol.platform.rental.application.internal.outboundservices.acl;

import com.andesheavytech.maquicontrol.platform.fleet.interfaces.acl.FleetContextFacade;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Anti-corruption layer towards the Fleet Management bounded context. Translates Fleet's
 * primitive answers into Rental's own types.
 */
@Service
public class ExternalFleetService {

    private final FleetContextFacade fleetContextFacade;

    public ExternalFleetService(FleetContextFacade fleetContextFacade) {
        this.fleetContextFacade = fleetContextFacade;
    }

    public boolean existsMachinery(Long machineryId) {
        return fleetContextFacade.existsMachinery(machineryId);
    }

    public boolean isMachineryRentable(Long machineryId) {
        return fleetContextFacade.isMachineryRentable(machineryId);
    }

    public Optional<Long> fetchOwnerProfileId(Long machineryId) {
        var ownerProfileId = fleetContextFacade.fetchOwnerProfileId(machineryId);
        return ownerProfileId == null || ownerProfileId == 0L ? Optional.empty() : Optional.of(ownerProfileId);
    }

    public Optional<Money> fetchHourlyRate(Long machineryId) {
        var amount = fleetContextFacade.fetchHourlyRateAmount(machineryId);
        var currency = fleetContextFacade.fetchHourlyRateCurrency(machineryId);
        if (amount == null || currency == null || currency.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new Money(amount, currency));
    }

    public List<Long> fetchMachineryIdsByOwnerProfileId(Long ownerProfileId) {
        return fleetContextFacade.fetchMachineryIdsByOwnerProfileId(ownerProfileId);
    }
}
