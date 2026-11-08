package com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.resources;

import java.time.LocalDateTime;

public record RentalResource(Long id, Long machineryId, Long contractorProfileId, RentalPeriodResource period,
                             long durationInDays, String status, RentalAmountResource totalAmount,
                             String cancellationReason, LocalDateTime requestedAt, LocalDateTime confirmedAt,
                             LocalDateTime cancelledAt) {
}
