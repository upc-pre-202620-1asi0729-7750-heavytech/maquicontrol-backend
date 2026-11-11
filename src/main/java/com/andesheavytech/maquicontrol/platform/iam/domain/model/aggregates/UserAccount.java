package com.andesheavytech.maquicontrol.platform.iam.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.entities.Role;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.events.UserSignedUpEvent;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.AccountStatus;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.Credential;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.EmailAddress;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.RoleName;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * User account aggregate root.
 *
 * <p>Owns the login email, the hashed credential, the account status and the roles.
 * No JPA annotation lives here: persistence is handled by {@code UserAccountPersistenceEntity}.</p>
 */
@Getter
public class UserAccount extends AbstractDomainAggregateRoot<UserAccount> {

    private Long id;
    private final EmailAddress email;
    private Credential credential;
    private AccountStatus status;
    private final Set<Role> roles;
    private LocalDateTime lastLoginAt;

    private UserAccount(Long id, EmailAddress email, Credential credential, AccountStatus status,
                        Set<Role> roles, LocalDateTime lastLoginAt) {
        this.id = id;
        this.email = Objects.requireNonNull(email, "email is required");
        this.credential = Objects.requireNonNull(credential, "credential is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.roles = new HashSet<>(roles == null ? Set.of() : roles);
        this.lastLoginAt = lastLoginAt;
    }

    /**
     * Creates a new, active account with the role chosen on sign up.
     *
     * @param email      the login email
     * @param credential the hashed credential
     * @param role       the chosen role
     * @return the new account
     */
    public static UserAccount signUp(EmailAddress email, Credential credential, Role role) {
        Objects.requireNonNull(role, "role is required");
        if (!role.getName().isSelfAssignable()) {
            throw new IllegalArgumentException("role %s cannot be chosen on sign up".formatted(role.getName()));
        }
        return new UserAccount(null, email, credential, AccountStatus.ACTIVE, Set.of(role), null);
    }

    /**
     * Rebuilds an existing account from persistence.
     */
    public static UserAccount reconstitute(Long id, EmailAddress email, Credential credential, AccountStatus status,
                                           Set<Role> roles, LocalDateTime lastLoginAt) {
        return new UserAccount(id, email, credential, status, roles, lastLoginAt);
    }

    /**
     * Records a successful sign in. The password itself is verified by the hashing service.
     *
     * @throws IllegalStateException when the account is not active
     */
    public void signIn() {
        if (!isActive()) {
            throw new IllegalStateException("account is %s".formatted(status));
        }
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * Registers the {@link UserSignedUpEvent}. Called by the repository adapter once the id is assigned.
     */
    public void onSignedUp() {
        var roleName = roles.stream().findFirst().map(role -> role.getName().name()).orElse(null);
        registerDomainEvent(new UserSignedUpEvent(id, email.value(), roleName));
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    public boolean hasRole(RoleName roleName) {
        return roles.stream().anyMatch(role -> role.getName() == roleName);
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public String getEmailValue() {
        return email.value();
    }
}
