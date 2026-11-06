package com.andesheavytech.maquicontrol.platform.rental.infrastructure.persistence;
import com.andesheavytech.maquicontrol.platform.rental.domain.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
public interface RentalJpaRepository extends JpaRepository<RentalPersistenceEntity, Long> {
    List<RentalPersistenceEntity> findAllByContractorProfileId(Long contractorProfileId);
    List<RentalPersistenceEntity> findAllByMachineryId(Long machineryId);
    List<RentalPersistenceEntity> findAllByStatus(RentalStatus status);
    @Query("select count(r) > 0 from RentalPersistenceEntity r where r.machineryId = :machineryId and r.status in :statuses and r.startDate <= :endDate and r.endDate >= :startDate and (:excludedId is null or r.id <> :excludedId)")
    boolean existsOverlapping(@Param("machineryId") Long machineryId, @Param("statuses") Collection<RentalStatus> statuses, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("excludedId") Long excludedId);
}
