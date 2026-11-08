package com.andesheavytech.maquicontrol.platform.rental.domain.model.commands;

/**
 * Cancels a rental. The requester can be the contractor or the machine owner.
 */
public record CancelRentalCommand(Long rentalId, Long requesterProfileId, String reason) {
}
