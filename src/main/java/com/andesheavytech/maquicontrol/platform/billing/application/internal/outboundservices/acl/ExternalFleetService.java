package com.andesheavytech.maquicontrol.platform.billing.application.internal.outboundservices.acl;

import com.andesheavytech.maquicontrol.platform.fleet.interfaces.acl.FleetContextFacade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Anti-corruption layer towards the Fleet Management bounded context.
 */
@Service("billingExternalFleetService")
public class ExternalFleetService {

    private final FleetContextFacade fleetContextFacade;

    public ExternalFleetService(FleetContextFacade fleetContextFacade) {
        this.fleetContextFacade = fleetContextFacade;
    }

    public Optional<Long> fetchOwnerProfileId(Long machineryId) {
        var id = fleetContextFacade.fetchOwnerProfileId(machineryId);
        return id != null && id > 0 ? Optional.of(id) : Optional.empty();
    }

    public List<Long> fetchMachineryIdsByOwnerProfileId(Long ownerProfileId) {
        return fleetContextFacade.fetchMachineryIdsByOwnerProfileId(ownerProfileId);
    }

    public BigDecimal fetchHourlyRateAmount(Long machineryId) {
        return fleetContextFacade.fetchHourlyRateAmount(machineryId);
    }

    public String fetchHourlyRateCurrency(Long machineryId) {
        return fleetContextFacade.fetchHourlyRateCurrency(machineryId);
    }
}
