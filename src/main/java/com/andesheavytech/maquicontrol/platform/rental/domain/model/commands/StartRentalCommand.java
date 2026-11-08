package com.andesheavytech.maquicontrol.platform.rental.domain.model.commands;

/**
 * Policy command: no endpoint, issued when the start date arrives.
 */
public record StartRentalCommand(Long rentalId) {
}
