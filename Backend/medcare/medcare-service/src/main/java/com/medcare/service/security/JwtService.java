package com.medcare.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates and validates HS256-signed JWTs carrying non-role authorities as {@code scopes} claims.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    /**
     * Builds a signed JWT including non-role authorities as a {@code scopes} array claim.
     *
     * @param username    subject
     * @param authorities Spring authorities ({@code ROLE_*} entries are omitted from scopes)
     * @return compact serialized token
     */
    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        List<String> scopes = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> !a.startsWith("ROLE_"))
                .collect(Collectors.toList());
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .claim("scopes", scopes)
                .signWith(signingKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * @param token signed JWT
     * @return subject username
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * @param token    signed JWT
     * @param username expected subject
     * @return {@code true} if subject matches and token is not expired
     */
    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signingKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
