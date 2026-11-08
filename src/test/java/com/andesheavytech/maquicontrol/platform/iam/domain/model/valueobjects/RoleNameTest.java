package com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleNameTest {

    @Test
    void systemAdministratorCannotBeChosenOnSignUp() {
        assertFalse(RoleName.SYSTEM_ADMINISTRATOR.isSelfAssignable());
    }

    @Test
    void contractorAndFleetOwnerCanBeChosenOnSignUp() {
        assertTrue(RoleName.CONTRACTOR.isSelfAssignable());
        assertTrue(RoleName.FLEET_OWNER.isSelfAssignable());
    }
}
