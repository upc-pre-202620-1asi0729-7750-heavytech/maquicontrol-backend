package com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects;

import java.util.Locale;
import java.util.Objects;

/**
 * Identity document of a person. DNI has 8 digits; CE and passport have 6 to 12 letters or digits.
 *
 * @param type  the document type
 * @param value the document number
 */
public record DocumentNumber(DocumentType type, String value) {

    public DocumentNumber {
        Objects.requireNonNull(type, "documentType is required");
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("documentNumber is required");
        }
        value = value.trim().toUpperCase(Locale.ROOT);
        var valid = switch (type) {
            case DNI -> value.matches("\\d{8}");
            case CE, PASSPORT -> value.matches("[A-Z0-9]{6,12}");
        };
        if (!valid) {
            throw new IllegalArgumentException("documentNumber is not a valid %s".formatted(type));
        }
    }
}
