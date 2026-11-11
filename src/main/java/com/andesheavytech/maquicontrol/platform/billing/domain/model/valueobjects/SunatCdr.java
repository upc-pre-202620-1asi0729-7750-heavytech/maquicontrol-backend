package com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects;

import java.util.Objects;

/**
 * Value object representing a SUNAT Constancia de Recepción (CDR).
 */
public record SunatCdr(String value) {

    public SunatCdr {
        Objects.requireNonNull(value, "value is required");
        if (value.isBlank()) {
            throw new IllegalArgumentException("SUNAT CDR cannot be blank");
        }
        value = value.trim();
    }

    public boolean isAccepted() {
        return value.toUpperCase().contains("ACCEPTED") || value.toUpperCase().contains("ACEPTADO") || value.startsWith("CDR-0");
    }
}
