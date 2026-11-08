package com.andesheavytech.maquicontrol.platform.iam.domain.model.commands;

/**
 * Command to authenticate a user (Event Storming: Sign In).
 *
 * @param email    the login email
 * @param password the raw password
 */
public record SignInCommand(String email, String password) {
}
