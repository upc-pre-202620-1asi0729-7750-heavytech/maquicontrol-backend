package com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.jpa.entities;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.OrganizationType;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity for the {@code organizations} table.
 */
@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
public class OrganizationPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "legal_name", nullable = false, length = 150)
    private String legalName;

    @Column(name = "trade_name", nullable = false, length = 150)
    private String tradeName;

    @Column(name = "tax_id", nullable = false, unique = true, length = 11)
    private String taxId;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization_type", nullable = false, length = 30)
    private OrganizationType organizationType;

    @Column(name = "address", length = 200)
    private String address;
}
