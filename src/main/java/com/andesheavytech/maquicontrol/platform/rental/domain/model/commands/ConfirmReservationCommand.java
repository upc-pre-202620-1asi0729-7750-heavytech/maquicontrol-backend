package com.andesheavytech.maquicontrol.platform.rental.domain.model.commands;

public record ConfirmReservationCommand(Long rentalId, Long ownerProfileId) {
}
