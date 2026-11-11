package com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/**
 * Body of {@code PATCH /api/v1/rentals/{rentalId}/status}: CONFIRMED, REJECTED (with reason) or CANCELLED (with reason).
 */
public record UpdateRentalStatusResource(@NotBlank String status, String reason) {
}
