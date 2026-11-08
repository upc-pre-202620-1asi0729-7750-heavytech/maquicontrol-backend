package com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources;

public record ContactInformationResource(String phoneNumber, String secondaryEmail, String address,
                                         String district, String city) {
}
