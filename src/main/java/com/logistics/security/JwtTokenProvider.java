package com.logistics.security;

import com.logistics.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Handles JWT creation, parsing, and validation using JJWT 0.12.x fluent API.
 *
 * Token claims:
 *   sub  – contactInfo (email / username)
 *   role – UserRole string
 *   iat, exp – standard timestamps
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final JwtProperties jwtProperties;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String role = principal.getAuthorities().iterator().next().getAuthority()
                               .replace("ROLE_", "");
        return buildToken(principal.getUsername(), role);
    }

    public String generateToken(String username, String role) {
        return buildToken(username, role);
    }

    private String buildToken(String username, String role) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpirationMs());
        return Jwts.builder()
                   .subject(username)
                   .claim("role", role)
                   .issuedAt(now)
                   .expiration(expiry)
                   .signWith(signingKey())
                   .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                   .verifyWith(signingKey())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("JWT expired: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("Invalid JWT: {}", ex.getMessage());
        } catch (SecurityException ex) {
            log.warn("JWT signature invalid: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("JWT claims empty: {}", ex.getMessage());
        }
        return false;
    }
}
