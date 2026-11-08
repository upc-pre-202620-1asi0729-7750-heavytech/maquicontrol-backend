package com.andesheavytech.maquicontrol.platform.iam.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.entities.Role;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.AccountStatus;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.Credential;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.EmailAddress;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.RoleName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserAccountTest {

    @Test
    void signUpCreatesAnActiveAccountWithTheChosenRole() {
        var account = UserAccount.signUp(new EmailAddress("Ana@Mail.com"), Credential.of("hash"), new Role(RoleName.CONTRACTOR));
        assertEquals("ana@mail.com", account.getEmailValue());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertTrue(account.hasRole(RoleName.CONTRACTOR));
    }

    @Test
    void signUpRejectsTheSystemAdministratorRole() {
        assertThrows(IllegalArgumentException.class, () -> UserAccount.signUp(new EmailAddress("ana@mail.com"),
                Credential.of("hash"), new Role(RoleName.SYSTEM_ADMINISTRATOR)));
    }

    @Test
    void rejectsAnInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> new EmailAddress("ana-at-mail"));
    }
}
