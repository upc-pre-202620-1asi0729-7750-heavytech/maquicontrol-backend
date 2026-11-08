package com.andesheavytech.maquicontrol.platform.rental.interfaces.events;

/**
 * Published for other bounded contexts when a confirmed or running rental is cancelled or completed.
 *
 * @param machineryStillCommitted whether another confirmed or running rental keeps the machine committed
 */
public record RentalClosedIntegrationEvent(Long rentalId, Long machineryId, boolean machineryStillCommitted) {
}
