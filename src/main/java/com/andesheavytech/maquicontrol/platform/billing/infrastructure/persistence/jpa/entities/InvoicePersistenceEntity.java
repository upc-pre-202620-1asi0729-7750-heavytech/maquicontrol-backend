package com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.entities;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.InvoiceStatus;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA entity for the {@code invoices} table.
 */
@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
public class InvoicePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "rental_id", nullable = false, unique = true)
    private Long rentalId;

    @Column(name = "series", length = 10)
    private String series;

    @Column(name = "correlative")
    private Integer correlative;

    @Column(name = "sunat_cdr", length = 200)
    private String sunatCdr;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private InvoiceStatus status;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
}
