package com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MachineryLocationTest {

    @Test
    void acceptsAValidLocation() {
        assertDoesNotThrow(() -> new MachineryLocation("Lima", "Lima", "Ate", "Av. Nicolás Ayllón 123", -12.04, -76.93));
    }

    @Test
    void requiresTheDistrict() {
        assertThrows(IllegalArgumentException.class,
                () -> new MachineryLocation("Lima", "Lima", " ", "Av. Nicolás Ayllón 123", null, null));
    }

    @Test
    void rejectsALatitudeOutOfRange() {
        assertThrows(IllegalArgumentException.class,
                () -> new MachineryLocation("Lima", "Lima", "Ate", "Av. Nicolás Ayllón 123", 91.0, -76.93));
    }
}
