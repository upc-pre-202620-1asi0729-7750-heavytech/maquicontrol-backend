package com.andesheavytech.maquicontrol.iam.infrastructure.security;

import com.andesheavytech.maquicontrol.iam.domain.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenService {

    private final SecretKey signingKey;
    private final long expirationDays;

    public JwtTokenService(
            @Value("${authorization.jwt.secret}") String secret,
            @Value("${authorization.jwt.expiration.days:7}") long expirationDays
    ) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must contain at least 32 bytes"
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        this.expirationDays = expirationDays;
    }

    public String generateToken(UserAccount account) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(account.getId().toString())
                .claim(
                        "roles",
                        account.getRoles().stream()
                                .map(Enum::name)
                                .toList()
                )
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationDays, ChronoUnit.DAYS)))
                .signWith(signingKey)
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token);
    }
}