package com.andesheavytech.maquicontrol.platform.rental.domain.model.commands;

public record RejectRentalCommand(Long rentalId, Long ownerProfileId, String reason) {
}
