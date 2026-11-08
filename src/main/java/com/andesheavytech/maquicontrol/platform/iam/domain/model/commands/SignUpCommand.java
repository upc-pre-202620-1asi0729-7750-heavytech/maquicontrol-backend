package com.andesheavytech.maquicontrol.platform.iam.domain.model.commands;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.RoleName;

/**
 * Command to create a user account (Event Storming: Sign Up).
 *
 * @param email    the login email
 * @param password the raw password
 * @param roleName the role chosen by the visitor
 */
public record SignUpCommand(String email, String password, RoleName roleName) {
}
