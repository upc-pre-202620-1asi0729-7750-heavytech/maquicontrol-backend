package com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects;

/**
 * Lifecycle of a rental.
 */
public enum RentalStatus {
    REQUESTED,
    CONFIRMED,
    REJECTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    /**
     * Whether a rental in this status takes the machine for its dates.
     */
    public boolean blocksMachinery() {
        return this == REQUESTED || this == CONFIRMED || this == IN_PROGRESS;
    }

    /**
     * Whether a rental in this status keeps the machine reserved or rented.
     */
    public boolean commitsMachinery() {
        return this == CONFIRMED || this == IN_PROGRESS;
    }

    public boolean isClosed() {
        return this == REJECTED || this == COMPLETED || this == CANCELLED;
    }
}
