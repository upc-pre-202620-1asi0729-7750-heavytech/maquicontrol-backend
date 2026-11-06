package com.andesheavytech.maquicontrol.platform.rental.application;

import com.andesheavytech.maquicontrol.platform.fleet.domain.MachineryStatus;
import com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.MachineryJpaRepository;
import com.andesheavytech.maquicontrol.platform.rental.domain.RentalPeriod;
import com.andesheavytech.maquicontrol.platform.rental.domain.RentalStatus;
import com.andesheavytech.maquicontrol.platform.rental.infrastructure.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

/** Rental application service. The availability ACL is intentionally limited to Fleet's persistence adapter. */
@Service @Transactional
public class RentalService {
    private static final EnumSet<RentalStatus> BLOCKING_STATUSES=EnumSet.of(RentalStatus.REQUESTED,RentalStatus.CONFIRMED,RentalStatus.IN_PROGRESS);
    private final RentalJpaRepository rentals; private final MachineryJpaRepository machinery;
    public RentalService(RentalJpaRepository rentals,MachineryJpaRepository machinery){this.rentals=rentals;this.machinery=machinery;}
    public RentalPersistenceEntity request(Request data){var period=period(data.startDate(),data.endDate());var machine=machinery.findById(id(data.machineryId(),"machineryId")).orElseThrow(()->notFound("Machinery",data.machineryId()));if(machine.getStatus()!= MachineryStatus.AVAILABLE)throw conflict("Machinery is not available");if(rentals.existsOverlapping(machine.getId(),BLOCKING_STATUSES,period.startDate(),period.endDate(),null))throw conflict("Machinery already has an overlapping rental");return rentals.save(new RentalPersistenceEntity(machine.getId(),id(data.contractorProfileId(),"contractorProfileId"),period.startDate(),period.endDate(),total(machine.getHourlyRateAmount(),period),machine.getHourlyRateCurrency()));}
    public RentalPersistenceEntity confirm(Long id){var rental=find(id);if(rentals.existsOverlapping(rental.getMachineryId(),EnumSet.of(RentalStatus.CONFIRMED,RentalStatus.IN_PROGRESS),rental.getStartDate(),rental.getEndDate(),rental.getId()))throw conflict("Machinery was reserved by another rental");try{rental.confirm();return rental;}catch(IllegalStateException e){throw rule(e.getMessage());}}
    public RentalPersistenceEntity reject(Long id,String reason){try{var r=find(id);r.reject(required(reason,"reason"));return r;}catch(IllegalStateException e){throw rule(e.getMessage());}}
    public RentalPersistenceEntity cancel(Long id,String reason){try{var r=find(id);r.cancel(required(reason,"reason"));return r;}catch(IllegalStateException e){throw rule(e.getMessage());}}
    public RentalPersistenceEntity reschedule(Long id,LocalDate startDate,LocalDate endDate){var r=find(id);var p=period(startDate,endDate);if(rentals.existsOverlapping(r.getMachineryId(),BLOCKING_STATUSES,p.startDate(),p.endDate(),r.getId()))throw conflict("Machinery already has an overlapping rental");var rate=machinery.findById(r.getMachineryId()).orElseThrow(()->notFound("Machinery",r.getMachineryId())).getHourlyRateAmount();try{r.updatePeriod(p.startDate(),p.endDate(),total(rate,p));return r;}catch(IllegalStateException e){throw rule(e.getMessage());}}
    @Transactional(readOnly=true) public RentalPersistenceEntity find(Long id){return rentals.findById(id).orElseThrow(()->notFound("Rental",id));}
    @Transactional(readOnly=true) public List<RentalPersistenceEntity> list(Long contractorProfileId,Long machineryId,RentalStatus status){if(contractorProfileId!=null)return rentals.findAllByContractorProfileId(contractorProfileId);if(machineryId!=null)return rentals.findAllByMachineryId(machineryId);if(status!=null)return rentals.findAllByStatus(status);return rentals.findAll();}
    @Transactional(readOnly=true) public boolean available(Long machineryId,LocalDate startDate,LocalDate endDate){var p=period(startDate,endDate);return machinery.findById(machineryId).map(m->m.getStatus()==MachineryStatus.AVAILABLE&&!rentals.existsOverlapping(machineryId,BLOCKING_STATUSES,p.startDate(),p.endDate(),null)).orElse(false);}
    private static RentalPeriod period(LocalDate start,LocalDate end){try{return new RentalPeriod(start,end);}catch(IllegalArgumentException e){throw bad(e.getMessage());}} private static BigDecimal total(BigDecimal hourlyRate,RentalPeriod period){return hourlyRate.multiply(BigDecimal.valueOf(period.durationInDays())).multiply(BigDecimal.valueOf(8));} private static Long id(Long value,String field){if(value==null||value<=0)throw bad(field+" must be positive");return value;}private static String required(String v,String f){if(v==null||v.isBlank())throw bad(f+" is required");return v.trim();}private static ResponseStatusException bad(String d){return new ResponseStatusException(HttpStatus.BAD_REQUEST,d);}private static ResponseStatusException conflict(String d){return new ResponseStatusException(HttpStatus.CONFLICT,d);}private static ResponseStatusException rule(String d){return new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,d);}private static ResponseStatusException notFound(String type,Long id){return new ResponseStatusException(HttpStatus.NOT_FOUND,type+" not found: "+id);}
    public record Request(Long machineryId,Long contractorProfileId,LocalDate startDate,LocalDate endDate){}
}
