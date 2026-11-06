package com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence;

import com.andesheavytech.maquicontrol.platform.profiles.domain.OrganizationType;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity @Table(name = "organizations") @Getter
public class OrganizationPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false, length = 140) private String name;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private OrganizationType type;
    @Column(nullable = false, unique = true, length = 20) private String taxId;
    @Column(length = 160) private String email;
    @Column(length = 30) private String phone;
    @Column(length = 200) private String address;
    protected OrganizationPersistenceEntity() { }
    public OrganizationPersistenceEntity(String name, OrganizationType type, String taxId, String email, String phone, String address) { update(name, type, taxId, email, phone, address); }
    public void update(String name, OrganizationType type, String taxId, String email, String phone, String address) { this.name=name; this.type=type; this.taxId=taxId; this.email=email; this.phone=phone; this.address=address; }
}
