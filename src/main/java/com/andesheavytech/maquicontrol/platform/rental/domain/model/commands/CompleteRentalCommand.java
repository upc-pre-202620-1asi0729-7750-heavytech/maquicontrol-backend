package com.andesheavytech.maquicontrol.platform.rental.domain.model.commands;

/**
 * Policy command: no endpoint, issued when the end date has passed.
 */
public record CompleteRentalCommand(Long rentalId) {
}
