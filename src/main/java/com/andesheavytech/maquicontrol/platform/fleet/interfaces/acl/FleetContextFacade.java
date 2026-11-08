package com.andesheavytech.maquicontrol.platform.fleet.interfaces.acl;

import java.math.BigDecimal;
import java.util.List;

/**
 * Facade that other bounded contexts use to read Fleet data.
 */
public interface FleetContextFacade {

    boolean existsMachinery(Long machineryId);

    /**
     * Whether the machine exists and can take rental requests (not in maintenance or out of service).
     */
    boolean isMachineryRentable(Long machineryId);

    /**
     * Owner profile of the machine, or {@code 0L} when it does not exist.
     */
    Long fetchOwnerProfileId(Long machineryId);

    /**
     * Hourly rate amount, or {@code null} when the machine does not exist.
     */
    BigDecimal fetchHourlyRateAmount(Long machineryId);

    /**
     * Hourly rate currency code (PEN or USD), or an empty string when the machine does not exist.
     */
    String fetchHourlyRateCurrency(Long machineryId);

    /**
     * Ids of the machines published by the owner.
     */
    List<Long> fetchMachineryIdsByOwnerProfileId(Long ownerProfileId);
}
