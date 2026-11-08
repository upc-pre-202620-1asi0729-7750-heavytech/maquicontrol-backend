package com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.jpa.entities;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.DocumentType;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity for the {@code profiles} table. Contact data lives in {@code contact_information},
 * joined one to one by {@code profile_id}.
 */
@Entity
@Table(name = "profiles")
@SecondaryTable(name = "contact_information", pkJoinColumns = @PrimaryKeyJoinColumn(name = "profile_id"))
@Getter
@Setter
@NoArgsConstructor
public class ProfilePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "user_account_id", nullable = false, unique = true)
    private Long userAccountId;

    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 10)
    private DocumentType documentType;

    @Column(name = "document_number", nullable = false, unique = true, length = 12)
    private String documentNumber;

    @Column(table = "contact_information", name = "phone_number", length = 16)
    private String phoneNumber;

    @Column(table = "contact_information", name = "secondary_email", length = 254)
    private String secondaryEmail;

    @Column(table = "contact_information", name = "address", length = 200)
    private String address;

    @Column(table = "contact_information", name = "district", length = 80)
    private String district;

    @Column(table = "contact_information", name = "city", length = 80)
    private String city;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "organization_id")
    private OrganizationPersistenceEntity organization;
}
