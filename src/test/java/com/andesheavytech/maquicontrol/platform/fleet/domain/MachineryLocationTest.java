package com.andesheavytech.maquicontrol.platform.fleet.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MachineryLocationTest {
    @Test void acceptsCoordinatesInsideTheirGeographicRanges() {
        var location = new MachineryLocation("Lima", "Lima", "Ate", "Av. Metropolitana 120", -12.04318, -77.02824);
        assertEquals("Ate", location.district());
    }

    @Test void rejectsCoordinatesOutsideTheirGeographicRanges() {
        assertThrows(IllegalArgumentException.class, () -> new MachineryLocation("Lima", "Lima", "Ate", "Street", 91.0, 0.0));
    }
}
