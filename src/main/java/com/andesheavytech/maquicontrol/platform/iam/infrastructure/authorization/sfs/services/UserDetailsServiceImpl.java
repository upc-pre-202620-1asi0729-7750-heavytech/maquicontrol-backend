package com.andesheavytech.maquicontrol.platform.iam.infrastructure.authorization.sfs.services;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.EmailAddress;
import com.andesheavytech.maquicontrol.platform.iam.domain.repositories.UserAccountRepository;
import com.andesheavytech.maquicontrol.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Loads the signed-in account by its email.
 */
@Service(value = "defaultUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public UserDetailsServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userAccountRepository.findByEmail(new EmailAddress(username))
                .map(UserDetailsImpl::build)
                .orElseThrow(() -> new UsernameNotFoundException("User account not found: " + username));
    }
}
