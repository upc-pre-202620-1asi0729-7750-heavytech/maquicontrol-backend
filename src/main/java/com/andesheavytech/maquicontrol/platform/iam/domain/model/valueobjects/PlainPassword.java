package com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects;

/**
 * Raw password typed by the user, checked against the password policy before it is hashed.
 *
 * @param value the raw password
 */
public record PlainPassword(String value) {

    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 72;

    public PlainPassword {
        if (value == null || value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("password must have between %d and %d characters".formatted(MIN_LENGTH, MAX_LENGTH));
        }
    }

    @Override
    public String toString() {
        return "PlainPassword[****]";
    }
}
