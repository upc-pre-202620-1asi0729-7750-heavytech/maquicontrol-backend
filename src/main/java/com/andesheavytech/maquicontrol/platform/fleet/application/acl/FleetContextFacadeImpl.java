package com.andesheavytech.maquicontrol.platform.fleet.application.acl;

import com.andesheavytech.maquicontrol.platform.fleet.application.queryservices.FleetManagementQueryService;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Machinery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.queries.GetMachineryByIdQuery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.queries.GetMachineryByOwnerProfileIdQuery;
import com.andesheavytech.maquicontrol.platform.fleet.interfaces.acl.FleetContextFacade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link FleetContextFacade}.
 */
@Service
public class FleetContextFacadeImpl implements FleetContextFacade {

    private final FleetManagementQueryService fleetManagementQueryService;

    public FleetContextFacadeImpl(FleetManagementQueryService fleetManagementQueryService) {
        this.fleetManagementQueryService = fleetManagementQueryService;
    }

    @Override
    public boolean existsMachinery(Long machineryId) {
        return find(machineryId).isPresent();
    }

    @Override
    public boolean isMachineryRentable(Long machineryId) {
        return find(machineryId).map(Machinery::acceptsRentalRequests).orElse(false);
    }

    @Override
    public Long fetchOwnerProfileId(Long machineryId) {
        return find(machineryId).map(Machinery::getOwnerProfileId).orElse(0L);
    }

    @Override
    public BigDecimal fetchHourlyRateAmount(Long machineryId) {
        return find(machineryId).map(machinery -> machinery.getHourlyRate().amount()).orElse(null);
    }

    @Override
    public String fetchHourlyRateCurrency(Long machineryId) {
        return find(machineryId).map(machinery -> machinery.getHourlyRate().currency().name()).orElse("");
    }

    @Override
    public String fetchMachineryStatus(Long machineryId) {
        return find(machineryId).map(machinery -> machinery.getStatus().name()).orElse("");
    }

    @Override
    public List<Long> fetchMachineryIdsByOwnerProfileId(Long ownerProfileId) {
        return fleetManagementQueryService.handle(new GetMachineryByOwnerProfileIdQuery(ownerProfileId)).stream()
                .map(Machinery::getId)
                .toList();
    }

    private Optional<Machinery> find(Long machineryId) {
        if (machineryId == null) {
            return Optional.empty();
        }
        return fleetManagementQueryService.handle(new GetMachineryByIdQuery(machineryId));
    }
}
