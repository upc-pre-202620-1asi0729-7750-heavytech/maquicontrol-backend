package com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects;

/**
 * Currencies accepted by MaquiControl for rates, rental totals and invoices.
 */
public enum Currency {
    PEN,
    USD;

    /**
     * Parses a currency code, ignoring case.
     *
     * @param code the ISO 4217 code
     * @return the matching currency
     * @throws IllegalArgumentException when the code is not PEN or USD
     */
    public static Currency fromCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("currency is required");
        }
        try {
            return Currency.valueOf(code.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("currency must be PEN or USD");
        }
    }
}
