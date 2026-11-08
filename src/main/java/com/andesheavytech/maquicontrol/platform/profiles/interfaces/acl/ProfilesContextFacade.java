package com.andesheavytech.maquicontrol.platform.profiles.interfaces.acl;

/**
 * Facade that other bounded contexts use to read Profiles data.
 */
public interface ProfilesContextFacade {

    /**
     * Finds the profile of the signed-in email.
     *
     * @param email the email carried by the bearer token
     * @return the profile id, or {@code 0L} when the account has no profile yet
     */
    Long fetchProfileIdByUserEmail(String email);

    /**
     * Checks whether a profile exists.
     */
    boolean existsProfile(Long profileId);

    /**
     * Full name of the profile, or an empty string when it does not exist.
     */
    String fetchFullName(Long profileId);
}
