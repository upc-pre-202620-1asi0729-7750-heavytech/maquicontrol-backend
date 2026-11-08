package com.andesheavytech.maquicontrol.platform.profiles.application.internal.outboundservices.acl;

import com.andesheavytech.maquicontrol.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Anti-corruption layer towards the IAM bounded context.
 */
@Service
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public Optional<Long> fetchUserAccountIdByEmail(String email) {
        var id = iamContextFacade.fetchUserAccountIdByEmail(email);
        return id == null || id == 0L ? Optional.empty() : Optional.of(id);
    }
}
