package com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.transform;

import java.util.Arrays;
import java.util.Locale;

/**
 * Turns a request string into an enum constant, with a readable error when it does not match.
 */
public final class EnumFromStringAssembler {

    private EnumFromStringAssembler() {
    }

    public static <E extends Enum<E>> E toEnum(Class<E> type, String value, String field) {
        var normalized = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(type.getEnumConstants())
                .filter(constant -> constant.name().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("%s %s is not valid".formatted(field, value)));
    }
}
