package com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects;

import java.util.Objects;

/**
 * Value object representing an electronic invoice number (e.g. F001-00000001).
 */
public record InvoiceNumber(String series, Integer correlative) {

    public InvoiceNumber {
        Objects.requireNonNull(series, "series is required");
        Objects.requireNonNull(correlative, "correlative is required");
        if (series.isBlank()) {
            throw new IllegalArgumentException("series cannot be blank");
        }
        if (correlative <= 0) {
            throw new IllegalArgumentException("correlative must be positive");
        }
    }

    public static InvoiceNumber of(String formatted) {
        if (formatted == null || !formatted.contains("-")) {
            throw new IllegalArgumentException("invalid invoice number format, expected SERIES-CORRELATIVE");
        }
        String[] parts = formatted.split("-");
        return new InvoiceNumber(parts[0].trim(), Integer.parseInt(parts[1].trim()));
    }

    public String format() {
        return "%s-%08d".formatted(series, correlative);
    }
}
