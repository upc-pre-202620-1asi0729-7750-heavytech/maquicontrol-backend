package com.andesheavytech.maquicontrol.platform.iam.domain.model.queries;

/**
 * Query for the account of the signed-in user.
 *
 * @param email the email carried by the bearer token
 */
public record GetSignedInAccountQuery(String email) {
}
