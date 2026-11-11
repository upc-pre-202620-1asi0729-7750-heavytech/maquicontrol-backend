package com.andesheavytech.maquicontrol.platform.rental.infrastructure.persistence.jpa.entities;

import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalStatus;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Currency;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JPA entity for the {@code rentals} table. The dates live in {@code rental_periods},
 * joined one to one by {@code rental_id}.
 */
@Entity
@Table(name = "rentals")
@SecondaryTable(name = "rental_periods", pkJoinColumns = @PrimaryKeyJoinColumn(name = "rental_id"))
@Getter
@Setter
@NoArgsConstructor
public class RentalPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "machinery_id", nullable = false)
    private Long machineryId;

    @Column(name = "contractor_profile_id", nullable = false)
    private Long contractorProfileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RentalStatus status;

    @Column(name = "total_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "total_currency", nullable = false, length = 3)
    private Currency totalCurrency;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(table = "rental_periods", name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(table = "rental_periods", name = "end_date", nullable = false)
    private LocalDate endDate;
}
