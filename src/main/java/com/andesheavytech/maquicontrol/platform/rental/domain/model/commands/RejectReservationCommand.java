package com.andesheavytech.maquicontrol.platform.rental.domain.model.commands;

public record RejectReservationCommand(Long rentalId, Long ownerProfileId, String reason) {
}
