package com.andesheavytech.maquicontrol.platform.rental.interfaces.events;

/**
 * Published for other bounded contexts when a rental starts.
 */
public record RentalStartedIntegrationEvent(Long rentalId, Long machineryId) {
}
