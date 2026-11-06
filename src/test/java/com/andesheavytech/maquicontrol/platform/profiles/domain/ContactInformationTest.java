package com.andesheavytech.maquicontrol.platform.profiles.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContactInformationTest {
    @Test void acceptsPeruvianStylePhoneAndAddress() { var contact=new ContactInformation("+51987654321", "Av. Lima 123"); assertEquals("+51987654321",contact.phone()); }
    @Test void rejectsAnInvalidPhone() { assertThrows(IllegalArgumentException.class, () -> new ContactInformation("123", "Av. Lima 123")); }
    @Test void rejectsBlankAddress() { assertThrows(IllegalArgumentException.class, () -> new ContactInformation("987654321", " ")); }
}
