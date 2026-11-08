package com.andesheavytech.maquicontrol.platform.iam.infrastructure.hashing.bcrypt;

import com.andesheavytech.maquicontrol.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Hashing service that Spring Security can also use as its {@link PasswordEncoder}.
 */
public interface BCryptHashingService extends HashingService, PasswordEncoder {
}
