package com.andesheavytech.maquicontrol.platform.rental.interfaces.acl;

/**
 * Facade that other bounded contexts use to read Rental data.
 */
public interface RentalContextFacade {

    /**
     * Whether the contractor has a rental in progress for the machine.
     */
    boolean existsRentalInProgress(Long machineryId, Long contractorProfileId);

    /**
     * Whether a rental exists.
     */
    boolean existsRental(Long rentalId);

    /**
     * Machinery id of the rental, or 0L when not found.
     */
    Long fetchRentalMachineryId(Long rentalId);

    /**
     * Contractor profile id of the rental, or 0L when not found.
     */
    Long fetchRentalContractorProfileId(Long rentalId);

    /**
     * Status of the rental (REQUESTED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, REJECTED), or empty string when not found.
     */
    String fetchRentalStatus(Long rentalId);

    /**
     * Total amount of the rental, or null when not found.
     */
    java.math.BigDecimal fetchRentalTotalAmount(Long rentalId);

    /**
     * Currency code of the rental, or empty string when not found.
     */
    String fetchRentalCurrency(Long rentalId);

    /**
     * Agreed end date of the rental period, or null when not found.
     */
    java.time.LocalDate fetchRentalEndDate(Long rentalId);
}
