package com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Machinery category, for example "Excavadoras". The code is derived from the name.
 */
@Getter
public class Category extends AbstractDomainAggregateRoot<Category> {

    private final Long id;
    private String code;
    private String name;

    private Category(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public static Category create(String name) {
        var validName = validateName(name);
        return new Category(null, toCode(validName), validName);
    }

    public static Category reconstitute(Long id, String code, String name) {
        return new Category(id, code, name);
    }

    public void rename(String name) {
        this.name = validateName(name);
        this.code = toCode(this.name);
    }

    /**
     * Builds the code for a name: upper case, no accents, words joined by underscores.
     */
    public static String toCode(String name) {
        var withoutAccents = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return withoutAccents.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_").replaceAll("^_|_$", "");
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("category name is required");
        }
        var trimmed = name.trim();
        if (trimmed.length() > 80) {
            throw new IllegalArgumentException("category name must have at most 80 characters");
        }
        return trimmed;
    }
}
