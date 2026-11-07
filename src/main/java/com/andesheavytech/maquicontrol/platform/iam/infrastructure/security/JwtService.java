package com.andesheavytech.maquicontrol.platform.iam.infrastructure.security;

import com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.UserAccountPersistenceEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

@Service
public class JwtService {
    private final SecretKey signingKey; private final Duration expiration;
    public JwtService(@Value("${authorization.jwt.secret}") String secret, @Value("${authorization.jwt.expiration.days}") long expirationDays) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.expiration = Duration.ofDays(expirationDays);
    }
    public String issue(UserAccountPersistenceEntity account) {
        var now = Instant.now();
        var roles = account.getRoles().stream().map(role -> role.getName().name()).sorted().toList();
        return Jwts.builder().subject(account.getId().toString()).claim("email", account.getEmail()).claim("roles", roles).issuedAt(Date.from(now)).expiration(Date.from(now.plus(expiration))).signWith(signingKey).compact();
    }
    public Long accountId(String token) { return Long.valueOf(Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload().getSubject()); }
}
