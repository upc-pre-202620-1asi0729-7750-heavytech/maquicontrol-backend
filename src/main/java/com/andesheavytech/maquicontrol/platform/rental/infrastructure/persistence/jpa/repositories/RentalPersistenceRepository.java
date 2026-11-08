package com.andesheavytech.maquicontrol.platform.rental.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalStatus;
import com.andesheavytech.maquicontrol.platform.rental.infrastructure.persistence.jpa.entities.RentalPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface RentalPersistenceRepository extends JpaRepository<RentalPersistenceEntity, Long> {

    List<RentalPersistenceEntity> findByContractorProfileIdOrderByIdDesc(Long contractorProfileId);

    List<RentalPersistenceEntity> findByMachineryIdInAndStatusOrderByIdAsc(Collection<Long> machineryIds, RentalStatus status);

    List<RentalPersistenceEntity> findByStatusAndStartDateLessThanEqual(RentalStatus status, LocalDate date);

    List<RentalPersistenceEntity> findByStatusAndEndDateLessThan(RentalStatus status, LocalDate date);

    @Query("""
            select count(r) > 0 from RentalPersistenceEntity r
            where r.machineryId = :machineryId
              and r.status in :statuses
              and r.startDate <= :endDate
              and r.endDate >= :startDate
              and (:excludedId is null or r.id <> :excludedId)
            """)
    boolean existsOverlapping(@Param("machineryId") Long machineryId,
                              @Param("statuses") Collection<RentalStatus> statuses,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("excludedId") Long excludedId);

    @Query("""
            select count(r) > 0 from RentalPersistenceEntity r
            where r.machineryId = :machineryId
              and r.status in :statuses
              and (:excludedId is null or r.id <> :excludedId)
            """)
    boolean existsOtherInStatuses(@Param("machineryId") Long machineryId,
                                  @Param("statuses") Collection<RentalStatus> statuses,
                                  @Param("excludedId") Long excludedId);
}
