package com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.entities.Organization;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.TaxId;
import com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.jpa.entities.OrganizationPersistenceEntity;

public final class OrganizationPersistenceAssembler {

    private OrganizationPersistenceAssembler() {
    }

    public static Organization toDomainFromPersistence(OrganizationPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Organization(entity.getId(), entity.getLegalName(), entity.getTradeName(),
                new TaxId(entity.getTaxId()), entity.getOrganizationType(), entity.getAddress());
    }

    public static OrganizationPersistenceEntity toPersistenceFromDomain(Organization organization) {
        if (organization == null) {
            return null;
        }
        var entity = new OrganizationPersistenceEntity();
        if (organization.getId() != null) {
            entity.setId(organization.getId());
        }
        entity.setLegalName(organization.getLegalName());
        entity.setTradeName(organization.getTradeName());
        entity.setTaxId(organization.getTaxId().value());
        entity.setOrganizationType(organization.getType());
        entity.setAddress(organization.getAddress());
        return entity;
    }
}
