package com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContactInformationTest {

    @Test
    void acceptsAPeruvianMobileNumber() {
        assertDoesNotThrow(() -> new ContactInformation("+51987654321", null, "Av. Arequipa 123", "Miraflores", "Lima"));
    }

    @Test
    void rejectsAnInvalidPhoneNumber() {
        assertThrows(IllegalArgumentException.class,
                () -> new ContactInformation("98-76", null, null, null, null));
    }

    @Test
    void rejectsAnAddressLongerThan200Characters() {
        assertThrows(IllegalArgumentException.class,
                () -> new ContactInformation(null, null, "a".repeat(201), null, null));
    }
}
