package com.andesheavytech.maquicontrol.platform.profiles.domain.model.entities;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.OrganizationType;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.TaxId;
import lombok.Getter;

import java.util.Objects;

/**
 * Organization a profile belongs to. It lives inside the {@code Profile} aggregate.
 */
@Getter
public class Organization {

    private final Long id;
    private String legalName;
    private String tradeName;
    private TaxId taxId;
    private OrganizationType type;
    private String address;

    public Organization(Long id, String legalName, String tradeName, TaxId taxId, OrganizationType type, String address) {
        this.id = id;
        apply(legalName, tradeName, taxId, type, address);
    }

    public Organization(String legalName, String tradeName, TaxId taxId, OrganizationType type, String address) {
        this(null, legalName, tradeName, taxId, type, address);
    }

    public void update(String legalName, String tradeName, TaxId taxId, OrganizationType type, String address) {
        apply(legalName, tradeName, taxId, type, address);
    }

    private void apply(String legalName, String tradeName, TaxId taxId, OrganizationType type, String address) {
        if (legalName == null || legalName.isBlank() || legalName.trim().length() > 150) {
            throw new IllegalArgumentException("legalName is required and must have at most 150 characters");
        }
        if (tradeName != null && tradeName.trim().length() > 150) {
            throw new IllegalArgumentException("tradeName must have at most 150 characters");
        }
        if (address != null && address.trim().length() > 200) {
            throw new IllegalArgumentException("address must have at most 200 characters");
        }
        this.legalName = legalName.trim();
        this.tradeName = tradeName == null || tradeName.isBlank() ? this.legalName : tradeName.trim();
        this.taxId = Objects.requireNonNull(taxId, "taxId is required");
        this.type = Objects.requireNonNull(type, "organizationType is required");
        this.address = address == null || address.isBlank() ? null : address.trim();
    }
}
