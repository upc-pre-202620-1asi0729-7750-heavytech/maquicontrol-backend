package com.andesheavytech.maquicontrol.platform.rental.domain.model.commands;

public record ConfirmRentalCommand(Long rentalId, Long ownerProfileId) {
}
