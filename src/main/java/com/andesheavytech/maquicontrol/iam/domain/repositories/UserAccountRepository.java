package com.andesheavytech.maquicontrol.iam.domain.repositories;

import com.andesheavytech.maquicontrol.iam.domain.UserAccount;

public interface UserAccountRepository {

    boolean existsByEmail(String email);

    UserAccount save(UserAccount account);
}