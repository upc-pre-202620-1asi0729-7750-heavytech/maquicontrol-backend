package com.andesheavytech.maquicontrol.platform.rental.domain.model.events;

public record RentalRejectedEvent(Long rentalId, Long machineryId, Long contractorProfileId, String reason) {
}
