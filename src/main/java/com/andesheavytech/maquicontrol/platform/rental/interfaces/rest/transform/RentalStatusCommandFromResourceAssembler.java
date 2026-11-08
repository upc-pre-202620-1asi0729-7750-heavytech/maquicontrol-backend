package com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.CancelRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.ConfirmRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.RejectRentalCommand;
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
            case "CONFIRMED" -> new ConfirmRentalCommand(rentalId, profileId);
            case "REJECTED" -> new RejectRentalCommand(rentalId, profileId, resource.reason());
            case "CANCELLED" -> new CancelRentalCommand(rentalId, profileId, resource.reason());
            default -> throw new IllegalArgumentException(
                    "status must be CONFIRMED, REJECTED or CANCELLED; IN_PROGRESS and COMPLETED are set automatically");
        };
    }
}
