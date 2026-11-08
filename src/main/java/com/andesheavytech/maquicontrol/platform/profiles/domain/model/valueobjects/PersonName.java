package com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects;

/**
 * First and last name of a person.
 */
public record PersonName(String firstName, String lastName) {

    public static final int MAX_LENGTH = 60;

    public PersonName {
        firstName = requireText(firstName, "firstName");
        lastName = requireText(lastName, "lastName");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("%s is required".formatted(field));
        }
        var trimmed = value.trim();
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("%s must have at most %d characters".formatted(field, MAX_LENGTH));
        }
        return trimmed;
    }

    public String fullName() {
        return "%s %s".formatted(firstName, lastName);
    }
}
