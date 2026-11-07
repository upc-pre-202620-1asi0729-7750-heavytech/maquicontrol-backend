package com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence;

import com.andesheavytech.maquicontrol.platform.fleet.domain.MachineryStatus;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name = "machinery")
public class MachineryPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false) private Long ownerProfileId;
    @ManyToOne(optional = false, fetch = FetchType.EAGER) @JoinColumn(name = "category_id", nullable = false) private CategoryPersistenceEntity category;
    @Column(nullable = false, length = 120) private String name;
    @Column(length = 500) private String description;
    @Column(nullable = false, length = 80) private String brand;
    @Column(nullable = false, length = 80) private String model;
    @Column(nullable = false) private int manufactureYear;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal hourlyRateAmount;
    @Column(nullable = false, length = 3) private String hourlyRateCurrency;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private MachineryStatus status;
    @Column(nullable = false, length = 80) private String department;
    @Column(nullable = false, length = 80) private String province;
    @Column(nullable = false, length = 80) private String district;
    @Column(nullable = false, length = 200) private String address;
    private Double latitude; private Double longitude;
    @Version private Long version;
    protected MachineryPersistenceEntity() { }
    public MachineryPersistenceEntity(Long ownerProfileId, CategoryPersistenceEntity category, String name, String description, String brand, String model, int manufactureYear, BigDecimal hourlyRateAmount, String hourlyRateCurrency, String department, String province, String district, String address, Double latitude, Double longitude) {
        this.ownerProfileId=ownerProfileId; this.category=category; this.name=name; this.description=description; this.brand=brand; this.model=model; this.manufactureYear=manufactureYear; this.hourlyRateAmount=hourlyRateAmount; this.hourlyRateCurrency=hourlyRateCurrency; this.department=department; this.province=province; this.district=district; this.address=address; this.latitude=latitude; this.longitude=longitude; this.status=MachineryStatus.AVAILABLE;
    }
    public Long getOwnerProfileId(){return ownerProfileId;} public CategoryPersistenceEntity getCategory(){return category;} public String getName(){return name;} public String getDescription(){return description;} public String getBrand(){return brand;} public String getModel(){return model;} public int getManufactureYear(){return manufactureYear;} public BigDecimal getHourlyRateAmount(){return hourlyRateAmount;} public String getHourlyRateCurrency(){return hourlyRateCurrency;} public MachineryStatus getStatus(){return status;} public String getDepartment(){return department;} public String getProvince(){return province;} public String getDistrict(){return district;} public String getAddress(){return address;} public Double getLatitude(){return latitude;} public Double getLongitude(){return longitude;}
    public void update(CategoryPersistenceEntity category, String name, String description, String brand, String model, int manufactureYear, BigDecimal hourlyRateAmount, String hourlyRateCurrency, String department, String province, String district, String address, Double latitude, Double longitude) { this.category=category;this.name=name;this.description=description;this.brand=brand;this.model=model;this.manufactureYear=manufactureYear;this.hourlyRateAmount=hourlyRateAmount;this.hourlyRateCurrency=hourlyRateCurrency;this.department=department;this.province=province;this.district=district;this.address=address;this.latitude=latitude;this.longitude=longitude; }
    public void changeStatus(MachineryStatus status){this.status=status;}
}
