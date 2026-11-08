package com.andesheavytech.maquicontrol.platform.iam.interfaces.acl;

/**
 * Facade that other bounded contexts use to read IAM data.
 */
public interface IamContextFacade {

    /**
     * Finds the account id for the email carried by the bearer token.
     *
     * @param email the signed-in email
     * @return the account id, or {@code 0L} when no account matches
     */
    Long fetchUserAccountIdByEmail(String email);

    /**
     * Checks whether the account has the given role.
     *
     * @param userAccountId the account id
     * @param roleName      the role name, for example {@code FLEET_OWNER}
     * @return {@code true} when the account has the role
     */
    boolean hasRole(Long userAccountId, String roleName);
}
