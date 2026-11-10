package com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.CancelReservationCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.ConfirmReservationCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.RejectReservationCommand;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.resources.UpdateRentalStatusResource;

import java.util.Locale;

/**
 * Picks the command that matches the requested status.
 */
public final class RentalStatusCommandFromResourceAssembler {

    private RentalStatusCommandFromResourceAssembler() {
    }

    public static Object toCommandFromResource(Long rentalId, Long profileId, UpdateRentalStatusResource resource) {
        return switch (resource.status().trim().toUpperCase(Locale.ROOT)) {
            case "CONFIRMED" -> new ConfirmReservationCommand(rentalId, profileId);
            case "REJECTED" -> new RejectReservationCommand(rentalId, profileId, resource.reason());
            case "CANCELLED" -> new CancelReservationCommand(rentalId, profileId, resource.reason());
            default -> throw new IllegalArgumentException(
                    "status must be CONFIRMED, REJECTED or CANCELLED; IN_PROGRESS and COMPLETED are set automatically");
        };
    }
}
