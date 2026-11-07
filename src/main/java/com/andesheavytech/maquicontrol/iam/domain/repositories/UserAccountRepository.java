package com.andesheavytech.maquicontrol.iam.domain.repositories;

import com.andesheavytech.maquicontrol.iam.domain.UserAccount;
import java.util.Optional;

public interface UserAccountRepository {

    boolean existsByEmail(String email);

    Optional<UserAccount> findByEmail(String email);

    UserAccount save(UserAccount account);
}