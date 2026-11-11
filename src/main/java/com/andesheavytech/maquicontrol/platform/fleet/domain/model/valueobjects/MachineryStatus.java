package com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects;

/**
 * Lifecycle of a machine in the fleet.
 */
public enum MachineryStatus {
    AVAILABLE,
    RESERVED,
    RENTED,
    IN_MAINTENANCE,
    OUT_OF_SERVICE;

    /**
     * Whether the owner may set this status by hand. RESERVED and RENTED only come from rentals.
     */
    public boolean isManuallyAssignable() {
        return this == AVAILABLE || this == IN_MAINTENANCE || this == OUT_OF_SERVICE;
    }

    /**
     * Whether the machine is committed to a rental.
     */
    public boolean isCommitted() {
        return this == RESERVED || this == RENTED;
    }
}
