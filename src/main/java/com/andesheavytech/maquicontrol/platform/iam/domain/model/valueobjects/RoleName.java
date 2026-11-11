package com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects;

/**
 * Roles recognized by MaquiControl access policies.
 */
public enum RoleName {
    CONTRACTOR,
    FLEET_OWNER,
    FLEET_ADMINISTRATOR,
    OPERATOR,
    SYSTEM_ADMINISTRATOR;

    /**
     * Whether a visitor may choose this role when signing up.
     *
     * @return {@code false} only for {@link #SYSTEM_ADMINISTRATOR}
     */
    public boolean isSelfAssignable() {
        return this != SYSTEM_ADMINISTRATOR;
    }

    /**
     * Default description shown when the role catalog is seeded.
     */
    public String defaultDescription() {
        return switch (this) {
            case CONTRACTOR -> "Rents machinery published by other users.";
            case FLEET_OWNER -> "Publishes and manages their own machinery.";
            case FLEET_ADMINISTRATOR -> "Manages the fleet and staff of an organization.";
            case OPERATOR -> "Operates machinery on behalf of an organization.";
            case SYSTEM_ADMINISTRATOR -> "Administers the MaquiControl platform.";
        };
    }
}
