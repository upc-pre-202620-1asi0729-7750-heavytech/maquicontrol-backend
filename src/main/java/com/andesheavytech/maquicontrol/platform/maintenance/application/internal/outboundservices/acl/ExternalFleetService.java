package com.andesheavytech.maquicontrol.platform.maintenance.application.internal.outboundservices.acl;

import com.andesheavytech.maquicontrol.platform.fleet.interfaces.acl.FleetContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Anti-corruption layer towards the Fleet Management bounded context.
 */
@Service("maintenanceExternalFleetService")
public class ExternalFleetService {

    private final FleetContextFacade fleetContextFacade;

    public ExternalFleetService(FleetContextFacade fleetContextFacade) {
        this.fleetContextFacade = fleetContextFacade;
    }

    public boolean existsMachinery(Long machineryId) {
        return fleetContextFacade.existsMachinery(machineryId);
    }

    public Optional<Long> fetchOwnerProfileId(Long machineryId) {
        var ownerProfileId = fleetContextFacade.fetchOwnerProfileId(machineryId);
        return ownerProfileId == null || ownerProfileId == 0L ? Optional.empty() : Optional.of(ownerProfileId);
    }

    /**
     * Whether the machine is reserved or rented, so it cannot go into maintenance now.
     */
    public boolean isMachineryCommittedToRental(Long machineryId) {
        var status = fleetContextFacade.fetchMachineryStatus(machineryId);
        return "RESERVED".equals(status) || "RENTED".equals(status);
    }

    public List<Long> fetchMachineryIdsByOwnerProfileId(Long ownerProfileId) {
        return fleetContextFacade.fetchMachineryIdsByOwnerProfileId(ownerProfileId);
    }
}
