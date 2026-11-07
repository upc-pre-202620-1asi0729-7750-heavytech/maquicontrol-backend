package com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence;

import com.andesheavytech.maquicontrol.platform.iam.domain.RoleName;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity @Table(name = "roles") @Getter
public class RolePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Enumerated(EnumType.STRING) @Column(nullable = false, unique = true, length = 40) private RoleName name;
    protected RolePersistenceEntity() { }
    public RolePersistenceEntity(RoleName name) { this.name = name; }
}
