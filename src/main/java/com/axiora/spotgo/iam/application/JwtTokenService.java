package com.axiora.spotgo.iam.application;

import com.axiora.spotgo.iam.domain.model.aggregates.UserAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenService.class);

    private final String secret;
    private final long expirationDays;

    public JwtTokenService(
            @Value("${authorization.jwt.secret}") String secret,
            @Value("${authorization.jwt.expiration.days}") long expirationDays) {
        this.secret = secret;
        this.expirationDays = expirationDays;
    }

    public String generateToken(UserAccount user) {
        var now = Instant.now();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("uid", user.getId())
                .claim("role", user.getRole().name().toLowerCase())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationDays, ChronoUnit.DAYS)))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSigningKey() {
        var bytes = secret.length() >= 32 ? secret.getBytes(StandardCharsets.UTF_8) : Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(bytes);
    }
}
