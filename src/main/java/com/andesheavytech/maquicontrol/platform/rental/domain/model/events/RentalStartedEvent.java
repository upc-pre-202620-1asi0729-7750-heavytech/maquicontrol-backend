package com.andesheavytech.maquicontrol.platform.rental.domain.model.events;

public record RentalStartedEvent(Long rentalId, Long machineryId, Long contractorProfileId) {
}
