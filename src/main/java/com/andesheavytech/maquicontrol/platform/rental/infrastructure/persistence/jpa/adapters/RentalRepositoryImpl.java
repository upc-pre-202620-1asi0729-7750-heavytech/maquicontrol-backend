package com.andesheavytech.maquicontrol.platform.rental.infrastructure.persistence.jpa.adapters;

import com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates.Rental;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalPeriod;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalStatus;
import com.andesheavytech.maquicontrol.platform.rental.domain.repositories.RentalRepository;
import com.andesheavytech.maquicontrol.platform.rental.infrastructure.persistence.jpa.assemblers.RentalPersistenceAssembler;
import com.andesheavytech.maquicontrol.platform.rental.infrastructure.persistence.jpa.entities.RentalPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.rental.infrastructure.persistence.jpa.repositories.RentalPersistenceRepository;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements {@link RentalRepository} with Spring Data JPA.
 */
@Repository
public class RentalRepositoryImpl implements RentalRepository {

    private final RentalPersistenceRepository rentalPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public RentalRepositoryImpl(RentalPersistenceRepository rentalPersistenceRepository,
                                DomainEventPublisher domainEventPublisher) {
        this.rentalPersistenceRepository = rentalPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Rental save(Rental rental) {
        var isNew = rental.getId() == null;
        var saved = rentalPersistenceRepository.save(RentalPersistenceAssembler.toPersistenceFromDomain(rental));
        var savedRental = RentalPersistenceAssembler.toDomainFromPersistence(saved);
        if (isNew) {
            savedRental.onRequested();
            domainEventPublisher.publishAndClearEvents(savedRental);
        } else {
            domainEventPublisher.publishAndClearEvents(rental);
        }
        return savedRental;
    }

    @Override
    public Optional<Rental> findById(Long id) {
        return rentalPersistenceRepository.findById(id).map(RentalPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Rental> findByContractorProfileId(Long contractorProfileId) {
        return toDomain(rentalPersistenceRepository.findByContractorProfileIdOrderByIdDesc(contractorProfileId));
    }

    @Override
    public List<Rental> findByMachineryIdsAndStatus(Collection<Long> machineryIds, RentalStatus status) {
        return toDomain(rentalPersistenceRepository.findByMachineryIdInAndStatusOrderByIdAsc(machineryIds, status));
    }

    @Override
    public boolean existsOverlapping(Long machineryId, Collection<RentalStatus> statuses, RentalPeriod period,
                                     Long excludedRentalId) {
        return rentalPersistenceRepository.existsOverlapping(machineryId, statuses, period.startDate(),
                period.endDate(), excludedRentalId);
    }

    @Override
    public boolean existsOtherInStatuses(Long machineryId, Collection<RentalStatus> statuses, Long excludedRentalId) {
        return rentalPersistenceRepository.existsOtherInStatuses(machineryId, statuses, excludedRentalId);
    }

    @Override
    public List<Rental> findConfirmedStartingOnOrBefore(LocalDate date) {
        return toDomain(rentalPersistenceRepository.findByStatusAndStartDateLessThanEqual(RentalStatus.CONFIRMED, date));
    }

    @Override
    public List<Rental> findInProgressEndingBefore(LocalDate date) {
        return toDomain(rentalPersistenceRepository.findByStatusAndEndDateLessThan(RentalStatus.IN_PROGRESS, date));
    }

    private static List<Rental> toDomain(List<RentalPersistenceEntity> entities) {
        return entities.stream().map(RentalPersistenceAssembler::toDomainFromPersistence).toList();
    }
}
