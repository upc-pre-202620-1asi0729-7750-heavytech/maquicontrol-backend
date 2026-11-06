package com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence;

import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity @Table(name = "profiles") @Getter
public class ProfilePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false, unique = true) private Long userAccountId;
    @Column(nullable = false, length = 80) private String firstName;
    @Column(nullable = false, length = 80) private String lastName;
    @Column(nullable = false, length = 30) private String phone;
    @Column(nullable = false, length = 200) private String address;
    @Column(length = 30) private String documentNumber;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "organization_id") private OrganizationPersistenceEntity organization;
    protected ProfilePersistenceEntity() { }
    public ProfilePersistenceEntity(Long userAccountId, String firstName, String lastName, String phone, String address) { this.userAccountId=userAccountId; update(firstName,lastName,phone,address,null); }
    public void update(String firstName, String lastName, String phone, String address, String documentNumber) { this.firstName=firstName;this.lastName=lastName;this.phone=phone;this.address=address;this.documentNumber=documentNumber; }
    public void join(OrganizationPersistenceEntity organization) { this.organization=organization; }
}
