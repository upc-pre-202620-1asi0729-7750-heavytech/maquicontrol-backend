package com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects.MachineryLocation;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects.MachinerySpecification;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects.MachineryStatus;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MachineryTest {

    private Machinery newMachinery() {
        return Machinery.publish(1L, 1L, "Excavadora CAT 320", "Excavadora hidráulica",
                new MachinerySpecification("Caterpillar", "320", 2020),
                new Money(new BigDecimal("180"), "PEN"),
                new MachineryLocation("Lima", "Lima", "Ate", "Av. Nicolás Ayllón 123", null, null));
    }

    @Test
    void startsAvailable() {
        assertEquals(MachineryStatus.AVAILABLE, newMachinery().getStatus());
    }

    @Test
    void ownerCannotSetReservedByHand() {
        assertThrows(IllegalArgumentException.class, () -> newMachinery().changeStatus(MachineryStatus.RESERVED));
    }

    @Test
    void aReservedMachineCannotBeDeletedNorChangedByHand() {
        var machinery = newMachinery();
        machinery.reserve();
        assertFalse(machinery.canBeDeleted());
        assertThrows(IllegalStateException.class, () -> machinery.changeStatus(MachineryStatus.IN_MAINTENANCE));
    }

    @Test
    void rejectsAManufactureYearBefore1950() {
        assertThrows(IllegalArgumentException.class, () -> new MachinerySpecification("Caterpillar", "320", 1949));
    }
}
