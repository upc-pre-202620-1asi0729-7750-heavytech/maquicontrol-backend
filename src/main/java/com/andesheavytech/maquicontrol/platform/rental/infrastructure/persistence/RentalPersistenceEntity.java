package com.andesheavytech.maquicontrol.platform.rental.infrastructure.persistence;

import com.andesheavytech.maquicontrol.platform.rental.domain.RentalStatus;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "rentals")
public class RentalPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false) private Long machineryId;
    @Column(nullable = false) private Long contractorProfileId;
    @Column(nullable = false) private LocalDate startDate;
    @Column(nullable = false) private LocalDate endDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private RentalStatus status;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal totalAmount;
    @Column(nullable = false, length = 3) private String totalCurrency;
    @Column(length = 300) private String cancellationReason;
    @Column(nullable = false) private LocalDateTime requestedAt;
    private LocalDateTime confirmedAt; private LocalDateTime cancelledAt; @Version private Long version;
    protected RentalPersistenceEntity() { }
    public RentalPersistenceEntity(Long machineryId, Long contractorProfileId, LocalDate startDate, LocalDate endDate, BigDecimal totalAmount, String totalCurrency) { this.machineryId=machineryId;this.contractorProfileId=contractorProfileId;this.startDate=startDate;this.endDate=endDate;this.totalAmount=totalAmount;this.totalCurrency=totalCurrency;this.status=RentalStatus.REQUESTED;this.requestedAt=LocalDateTime.now(); }
    public Long getMachineryId(){return machineryId;} public Long getContractorProfileId(){return contractorProfileId;} public LocalDate getStartDate(){return startDate;} public LocalDate getEndDate(){return endDate;} public RentalStatus getStatus(){return status;} public BigDecimal getTotalAmount(){return totalAmount;} public String getTotalCurrency(){return totalCurrency;} public String getCancellationReason(){return cancellationReason;} public LocalDateTime getRequestedAt(){return requestedAt;} public LocalDateTime getConfirmedAt(){return confirmedAt;} public LocalDateTime getCancelledAt(){return cancelledAt;}
    public void confirm(){ if(status!=RentalStatus.REQUESTED) throw new IllegalStateException("Only requested rentals can be confirmed"); status=RentalStatus.CONFIRMED;confirmedAt=LocalDateTime.now(); }
    public void reject(String reason){ if(status!=RentalStatus.REQUESTED) throw new IllegalStateException("Only requested rentals can be rejected"); status=RentalStatus.REJECTED;cancellationReason=reason;cancelledAt=LocalDateTime.now(); }
    public void cancel(String reason){ if(status==RentalStatus.CANCELLED||status==RentalStatus.REJECTED||status==RentalStatus.COMPLETED) throw new IllegalStateException("This rental cannot be cancelled"); status=RentalStatus.CANCELLED;cancellationReason=reason;cancelledAt=LocalDateTime.now(); }
    public void updatePeriod(LocalDate startDate, LocalDate endDate, BigDecimal totalAmount){ if(status==RentalStatus.CANCELLED||status==RentalStatus.REJECTED||status==RentalStatus.COMPLETED) throw new IllegalStateException("This rental cannot be rescheduled");this.startDate=startDate;this.endDate=endDate;this.totalAmount=totalAmount; }
}
