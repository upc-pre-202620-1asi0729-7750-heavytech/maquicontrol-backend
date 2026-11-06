package com.andesheavytech.maquicontrol.platform.iam.domain;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleNameTest {
    @Test void includesTheDefaultContractorRole() { assertTrue(Arrays.asList(RoleName.values()).contains(RoleName.CONTRACTOR)); }
    @Test void includesTheFleetAdministratorRole() { assertTrue(Arrays.asList(RoleName.values()).contains(RoleName.FLEET_ADMINISTRATOR)); }
}
