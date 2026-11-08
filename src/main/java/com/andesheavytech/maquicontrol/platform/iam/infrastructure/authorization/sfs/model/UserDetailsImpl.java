package com.andesheavytech.maquicontrol.platform.iam.infrastructure.authorization.sfs.model;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.aggregates.UserAccount;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Spring Security view of a {@link UserAccount}. The username is the email and each role becomes
 * an authority named {@code ROLE_<ROLE_NAME>}.
 */
@Getter
@EqualsAndHashCode
public class UserDetailsImpl implements UserDetails {

    private final Long userAccountId;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long userAccountId, String username, String password, boolean enabled,
                           Collection<? extends GrantedAuthority> authorities) {
        this.userAccountId = userAccountId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(UserAccount account) {
        var authorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .toList();
        return new UserDetailsImpl(account.getId(), account.getEmailValue(),
                account.getCredential().passwordHash(), account.isActive(), authorities);
    }
}
