package com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.entities;

import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA entity for the {@code late_penalties} table.
 */
@Entity
@Table(name = "late_penalties")
@Getter
@Setter
@NoArgsConstructor
public class LatePenaltyPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "rental_id", nullable = false)
    private Long rentalId;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;
}
