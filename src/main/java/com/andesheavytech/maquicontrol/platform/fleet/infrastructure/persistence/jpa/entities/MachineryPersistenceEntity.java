package com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.entities;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects.MachineryStatus;
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

/**
 * JPA entity for the {@code machinery} table. The location lives in {@code machinery_locations},
 * joined one to one by {@code machinery_id}.
 */
@Entity
@Table(name = "machinery")
@SecondaryTable(name = "machinery_locations", pkJoinColumns = @PrimaryKeyJoinColumn(name = "machinery_id"))
@Getter
@Setter
@NoArgsConstructor
public class MachineryPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "owner_profile_id", nullable = false)
    private Long ownerProfileId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "brand", nullable = false, length = 60)
    private String brand;

    @Column(name = "model", nullable = false, length = 60)
    private String model;

    @Column(name = "manufacture_year", nullable = false)
    private Integer manufactureYear;

    @Column(name = "hourly_rate_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal hourlyRateAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "hourly_rate_currency", nullable = false, length = 3)
    private Currency hourlyRateCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MachineryStatus status;

    @Column(table = "machinery_locations", name = "department", length = 60)
    private String department;

    @Column(table = "machinery_locations", name = "province", length = 60)
    private String province;

    @Column(table = "machinery_locations", name = "district", length = 60)
    private String district;

    @Column(table = "machinery_locations", name = "address", length = 200)
    private String address;

    @Column(table = "machinery_locations", name = "latitude")
    private Double latitude;

    @Column(table = "machinery_locations", name = "longitude")
    private Double longitude;
}
