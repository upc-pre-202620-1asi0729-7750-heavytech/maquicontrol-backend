package com.andesheavytech.maquicontrol.platform.iam.infrastructure.tokens.jwt;

import com.andesheavytech.maquicontrol.platform.iam.application.internal.outboundservices.tokens.TokenService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Token service that can also read the bearer token from an HTTP request.
 */
public interface BearerTokenService extends TokenService {

    String getBearerTokenFrom(HttpServletRequest request);
}
