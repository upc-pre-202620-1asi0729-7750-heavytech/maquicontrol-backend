package com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.assemblers;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Machinery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects.MachineryLocation;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects.MachinerySpecification;
import com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.entities.MachineryPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;

public final class MachineryPersistenceAssembler {

    private MachineryPersistenceAssembler() {
    }

    public static Machinery toDomainFromPersistence(MachineryPersistenceEntity entity) {
        return Machinery.reconstitute(
                entity.getId(),
                entity.getOwnerProfileId(),
                entity.getCategoryId(),
                entity.getName(),
                entity.getDescription(),
                new MachinerySpecification(entity.getBrand(), entity.getModel(), entity.getManufactureYear()),
                new Money(entity.getHourlyRateAmount(), entity.getHourlyRateCurrency()),
                new MachineryLocation(entity.getDepartment(), entity.getProvince(), entity.getDistrict(),
                        entity.getAddress(), entity.getLatitude(), entity.getLongitude()),
                entity.getStatus());
    }

    public static MachineryPersistenceEntity toPersistenceFromDomain(Machinery machinery) {
        var entity = new MachineryPersistenceEntity();
        if (machinery.getId() != null) {
            entity.setId(machinery.getId());
        }
        entity.setOwnerProfileId(machinery.getOwnerProfileId());
        entity.setCategoryId(machinery.getCategoryId());
        entity.setName(machinery.getName());
        entity.setDescription(machinery.getDescription());
        entity.setBrand(machinery.getSpecification().brand());
        entity.setModel(machinery.getSpecification().model());
        entity.setManufactureYear(machinery.getSpecification().manufactureYear());
        entity.setHourlyRateAmount(machinery.getHourlyRate().amount());
        entity.setHourlyRateCurrency(machinery.getHourlyRate().currency());
        entity.setStatus(machinery.getStatus());
        var location = machinery.getLocation();
        entity.setDepartment(location.department());
        entity.setProvince(location.province());
        entity.setDistrict(location.district());
        entity.setAddress(location.address());
        entity.setLatitude(location.latitude());
        entity.setLongitude(location.longitude());
        return entity;
    }
}
