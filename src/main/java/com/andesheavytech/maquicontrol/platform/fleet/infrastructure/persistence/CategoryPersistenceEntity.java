package com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence;

import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(name = "uk_categories_code", columnNames = "code"))
public class CategoryPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false, length = 40) private String code;
    @Column(nullable = false, length = 80) private String name;
    @Column(nullable = false) private boolean active = true;
    protected CategoryPersistenceEntity() { }
    public CategoryPersistenceEntity(String code, String name) { this.code = code; this.name = name; }
    public String getCode() { return code; } public String getName() { return name; } public boolean isActive() { return active; }
    public void rename(String name) { this.name = name; } public void deactivate() { this.active = false; }
}
