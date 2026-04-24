package com.logistics.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds app.jwt.* from application.yml into a typed bean.
 * Avoids scattering @Value annotations across the codebase.
 */
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /** HS256 signing secret — must be ≥ 256 bits (32 chars) in production. */
    private String secret;

    /** Token TTL in milliseconds (default 24 h = 86_400_000 ms). */
    private long expirationMs = 86_400_000L;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public long getExpirationMs() { return expirationMs; }
    public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
}
