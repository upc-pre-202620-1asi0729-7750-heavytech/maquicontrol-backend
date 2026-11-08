package com.andesheavytech.maquicontrol.platform.iam.application.internal.outboundservices.tokens;

/**
 * Outbound port for issuing and reading bearer tokens.
 */
public interface TokenService {

    String generateToken(String username);

    String getUsernameFromToken(String token);

    boolean validateToken(String token);
}
