package com.andesheavytech.maquicontrol.platform.rental.interfaces.acl;

/**
 * Facade that other bounded contexts use to read Rental data.
 */
public interface RentalContextFacade {

    /**
     * Whether the contractor has a rental in progress for the machine.
     */
    boolean existsRentalInProgress(Long machineryId, Long contractorProfileId);
}
