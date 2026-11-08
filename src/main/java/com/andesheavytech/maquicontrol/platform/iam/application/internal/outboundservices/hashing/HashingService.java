package com.andesheavytech.maquicontrol.platform.iam.application.internal.outboundservices.hashing;

/**
 * Outbound port for password hashing.
 */
public interface HashingService {

    String encode(CharSequence rawPassword);

    boolean matches(CharSequence rawPassword, String encodedPassword);
}
