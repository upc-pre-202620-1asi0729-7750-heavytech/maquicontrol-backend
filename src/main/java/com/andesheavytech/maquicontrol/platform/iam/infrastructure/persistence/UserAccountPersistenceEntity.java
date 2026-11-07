package com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence;

import com.andesheavytech.maquicontrol.platform.iam.domain.UserAccountStatus;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import java.util.*;

@Entity @Table(name = "user_accounts") @Getter
public class UserAccountPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false, unique = true, length = 160) private String email;
    @Column(nullable = false, length = 100) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private UserAccountStatus status;
    @ManyToMany(fetch = FetchType.EAGER) @JoinTable(name = "user_account_roles", joinColumns = @JoinColumn(name = "user_account_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RolePersistenceEntity> roles = new HashSet<>();
    protected UserAccountPersistenceEntity() { }
    public UserAccountPersistenceEntity(String email, String passwordHash, RolePersistenceEntity role) { this.email = email; this.passwordHash = passwordHash; this.status = UserAccountStatus.ACTIVE; this.roles.add(role); }
    public void changePassword(String passwordHash) { this.passwordHash = passwordHash; }
    public void assignRole(RolePersistenceEntity role) { this.roles.add(role); }
    public boolean isActive() { return status == UserAccountStatus.ACTIVE; }
}
