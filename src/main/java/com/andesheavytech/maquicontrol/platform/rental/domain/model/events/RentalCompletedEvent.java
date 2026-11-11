package com.andesheavytech.maquicontrol.platform.rental.domain.model.events;

public record RentalCompletedEvent(Long rentalId, Long machineryId, Long contractorProfileId) {
}
