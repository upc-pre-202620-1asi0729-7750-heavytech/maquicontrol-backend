package com.andesheavytech.maquicontrol.platform.rental.domain.model.events;

public record RentalConfirmedEvent(Long rentalId, Long machineryId, Long contractorProfileId) {
}
