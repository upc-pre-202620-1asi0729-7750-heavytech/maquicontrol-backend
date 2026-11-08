package com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.transform;

import java.util.Arrays;
import java.util.Locale;

/**
 * Turns a request string into one of the Maintenance enums, with a readable error.
 */
public final class MaintenanceEnumAssembler {

    private MaintenanceEnumAssembler() {
    }

    public static <E extends Enum<E>> E toEnum(Class<E> type, String value, String field) {
        var normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(type.getEnumConstants())
                .filter(constant -> constant.name().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("%s %s is not valid".formatted(field, value)));
    }
}
