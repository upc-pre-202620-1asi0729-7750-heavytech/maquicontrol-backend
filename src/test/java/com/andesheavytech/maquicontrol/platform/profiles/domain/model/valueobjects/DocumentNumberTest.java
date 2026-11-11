package com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentNumberTest {

    @Test
    void acceptsAnEightDigitDni() {
        assertDoesNotThrow(() -> new DocumentNumber(DocumentType.DNI, "71234567"));
    }

    @Test
    void rejectsADniWithLetters() {
        assertThrows(IllegalArgumentException.class, () -> new DocumentNumber(DocumentType.DNI, "7123456A"));
    }

    @Test
    void rejectsAnInvalidRuc() {
        assertThrows(IllegalArgumentException.class, () -> new TaxId("12345"));
    }
}
