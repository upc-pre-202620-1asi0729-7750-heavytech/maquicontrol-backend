package com.andesheavytech.maquicontrol.platform.rental.domain.model.events;

public record RentalRequestedEvent(Long rentalId, Long machineryId, Long contractorProfileId) {
}
