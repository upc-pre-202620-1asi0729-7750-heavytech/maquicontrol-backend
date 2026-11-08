package com.andesheavytech.maquicontrol.platform.rental.interfaces.events;

/**
 * Published for other bounded contexts when an owner confirms a rental.
 */
public record RentalConfirmedIntegrationEvent(Long rentalId, Long machineryId) {
}
