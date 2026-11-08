package com.andesheavytech.maquicontrol.platform.rental.domain.repositories;

import com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates.Rental;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalPeriod;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalStatus;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RentalRepository {

    Rental save(Rental rental);

    Optional<Rental> findById(Long id);

    List<Rental> findByContractorProfileId(Long contractorProfileId);

    List<Rental> findByMachineryIdsAndStatus(Collection<Long> machineryIds, RentalStatus status);

    /**
     * Whether another rental of the machine, in one of the statuses, overlaps the period.
     *
     * @param excludedRentalId rental to ignore, or {@code null}
     */
    boolean existsOverlapping(Long machineryId, Collection<RentalStatus> statuses, RentalPeriod period, Long excludedRentalId);

    /**
     * Whether another rental of the machine is in one of the statuses.
     */
    boolean existsOtherInStatuses(Long machineryId, Collection<RentalStatus> statuses, Long excludedRentalId);

    List<Rental> findConfirmedStartingOnOrBefore(LocalDate date);

    List<Rental> findInProgressEndingBefore(LocalDate date);
}
