package com.andesheavytech.maquicontrol.iam.application;

import com.andesheavytech.maquicontrol.iam.domain.UserAccount;
import java.util.UUID;

public class RegisterUserService {

    public UserAccount register(String email, String passwordHash) {
        return new UserAccount(UUID.randomUUID(), email, passwordHash);
    }
}