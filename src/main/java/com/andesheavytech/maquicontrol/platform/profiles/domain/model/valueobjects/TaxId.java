package com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects;

/**
 * Peruvian RUC: 11 digits starting with 10, 15, 17 or 20.
 *
 * @param value the RUC
 */
public record TaxId(String value) {

    public TaxId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("taxId is required");
        }
        value = value.trim();
        if (!value.matches("(10|15|17|20)\\d{9}")) {
            throw new IllegalArgumentException("taxId must be a valid 11 digit RUC");
        }
    }
}
